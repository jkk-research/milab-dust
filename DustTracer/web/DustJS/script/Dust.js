
if (!('Dust' in window)) {
	var KnowledgeMap = {};
	var Relations = new Set();

	function DustInit() {
		this.lookup = function(key, createIfMissing) {
			var id = key.split(' ')[0];
			var ret = KnowledgeMap[id];

			if (!ret && createIfMissing) {
				ret = { 'id' : id, 'label' : key.split(' ')[1]};
				
				if ( !ret.label ) {
					console.log('Missing label ' + key);
				}
				KnowledgeMap[id] = ret;
			}

			return ret;
		}
		
		this.isRelation = function(key) {
			return Relations.has(key);
		}
	}

	function DustSrv() {
		var requestId = 0;

		function doSend(request) {
			request.beforeSend = function(jqXHR, settings) {
				jqXHR.DustRequestId = ++requestId;
				jqXHR.DustMethod = request.method;
				jqXHR.DustURL = request.url;
				jqXHR.DustProc = request.respProc;

				console.log('Request ' + jqXHR.DustRequestId + ' sending...');
			};

			$.ajax(request)
				.done(function(data, textStatus, jqXHR) {
					if (jqXHR.DustProc) {

						var ct = jqXHR.getResponseHeader('content-type');

						var ids = [];

						if (ct.includes('application/vnd.api+json')) {
							var respArr = data.data;

							for (item of respArr) {
								var kItem = Dust.lookup(item.id, true);
								ids.push(kItem.id);

								if (item.attributes) {
									for (const key in item.attributes) {
										var val = item.attributes[key];

										var kAtt = Dust.lookup(key, true);
										kItem[kAtt.id] = val;
									}
								}

								if (item.relationships) {
									for (const key in item.relationships) {
										var rel = item.relationships[key];
										var val = null;
										
										if (Array.isArray(rel) ) {
											var ob;
											for ( target of rel ) {
												if ( !val ) {
													if ( target.meta ) {
														val = {};
														ob = true;
													} else {
														val = [];
														ob = false;
													}
												}
												
												var v = target.id; 
												Dust.lookup(v, true);
												if ( ob ) {
													var rk = target.meta.key;
													Dust.lookup(rk, true);
													val[rk] = v;
												} else {
													val.push(v);
												}
											}
										} else {
											val = rel.id;
											Dust.lookup(val, true);
										}
										
										var kRel = Dust.lookup(key, true).id;
										Relations.add(kRel);
										
										kItem[kRel] = val;
									}
								}
							}
						}

						var listeners = Array.isArray(jqXHR.DustProc) ? jqXHR.DustProc : [jqXHR.DustProc];

						for (l of listeners) {
							l(jqXHR.DustRequestId, true, textStatus, data, ids);
						}
					} else {
						console.log('Request ' + jqXHR.DustRequestId + ' done.');
					}
				})
				.fail(function(jqXHR, textStatus, errorThrown) {
					if (jqXHR.DustProc) {
						jqXHR.DustProc(jqXHR.DustRequestId, false, textStatus, errorThrown);
					} else {
						console.log('Request ' + jqXHR.DustRequestId + ' failed: ' + errorThrown);
					}
				});
		}

		this.sendData = function(cmd, content, request) {
			if (!request) {
				request = {};
			}

			request.method = 'POST';
			request.url = '/cmd/send';

			var data = {
				cmd: cmd,
				data: content
			}

			request.data = JSON.stringify(data);

			doSend(request);
		};

		this.loadResource = function(request) {
			request.method = 'GET';
			doSend(request);
		}
	}

	Dust = new DustInit();
	Dust.Srv = new DustSrv();

	console.log('Dust initialized.');
}
