
if ('Dust' in window) {
	function DustGraphInit() {

		var cy = cytoscape({
			container: document.getElementById('graph'),
			style: [
				{
					selector: 'node',
					style: {
						'label': 'data(label)'
					}
				},
				{
					selector: 'edge',
					style: {
						'label': 'data(label)',
						'curve-style': 'bezier',
						'target-arrow-shape': 'triangle'
					}
				}
			]
		});
		
		cy.on('select unselect tapselect tapunselect', 'node', function(evt){
			var sel = cy.$(':selected');
			var log = 'Selected: [';	
			var fs = '';		
			for (s of sel) {
				var item = Dust.lookup(s.id());
				log = log.concat( fs, '(', item.id, ' ', item.label, ' : ', item[DustHandles.MIND_ATT_KNOWLEDGE_PRIMARYASPECT],')' );
				fs = ', ';
			}
			console.log(log + ']');
		});

		this.loadResponseData = function(id, success, status, txt, ids) {
			cy.startBatch();

			var all = cy.elements('node[id]');
			cy.remove(all);

			var item;

			var items = {};

			for (itemId of ids) {
				item = Dust.lookup(itemId);
				if (!items[itemId]) {
					items[itemId] = cy.add({ group: 'nodes', data: { id: itemId, label: item.label } });
				}
			}

			for (itemId of ids) {
				item = Dust.lookup(itemId);

				for (const key in item) {
					if (Dust.isRelation(key)) {
						var rel = item[key];
						var isArr = Array.isArray(rel);
						var ri = Dust.lookup(key);
						
						if ( ri.id == DustHandles.MIND_ATT_KNOWLEDGE_PRIMARYASPECT ) {
							continue;
						}
						
						var rl = ri.label;
						var rk = null;

						if (isArr) {
							rk = 0;
						} else {
							if (typeof rel === "string") {
								rel = [rel];
							} else {
								rk = "";
							}
						}


						for (tr in rel) {
							var targetId = rel[tr];
							var target = Dust.lookup(targetId, true);
							targetId = target.id;

							if (!items[targetId]) {
								items[targetId] = cy.add({ group: 'nodes', data: { id: targetId, label: target.label } });
							}

							var l = rl;

							if (rk != null) {
								if (isArr) {
									l = l + ' [' + rk + ']';
									rk = rk + 1;
								} else {
									rk = tr;
									l = l + ' {' + rk + '}';
								}
							}
							
							cy.add({ group: 'edges', data: { source: itemId, target: targetId, label: l } });
						}
					}
				}
			}

			cy.endBatch();

			let options = {
				name: 'cose',

				ready: function() { },
				stop: function() { },

				animate: true,

				animationEasing: undefined,
				animationDuration: undefined,

				animateFilter: function(node, i) { return true; },
				animationThreshold: 250,
				refresh: 2,
				fit: true,
				padding: 30,

				boundingBox: undefined,
				nodeDimensionsIncludeLabels: false,

				randomize: false,
				componentSpacing: 40,
				nodeRepulsion: function(node) { return 2048; },
				nodeOverlap: 4,
				idealEdgeLength: function(edge) { return 32; },
				edgeElasticity: function(edge) { return 32; },
				nestingFactor: 1.2,
				gravity: 1,
				numIter: 1000,
				initialTemp: 1000,
				coolingFactor: 0.99,
				minTemp: 1.0
			};

			var layout = cy.layout(options);
			layout.run();
		}
	}

	Dust.Graph = new DustGraphInit();

	console.log('Dust Cytoscape graph initialized.');
}
