
if ( 'Dust' in window ){
	function DustGraphInit () {
	
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

	this.loadResponseData = function(id, success, status, txt, ids)  {	
			cy.startBatch();
			
			var all = cy.elements('node[id]');
			cy.remove(all);
			
			var item;
			
			var items = {};
			
			for ( itemId of ids ) {
				item = Dust.lookup(itemId);
				if ( !items[itemId] ) {
					items[itemId] = cy.add({ group: 'nodes', data: { id: itemId, label: item.id.split(' ')[1] }});
				}
			}
			
			for ( itemId of ids ) {
				item = Dust.lookup(itemId);
				var rel = item.relationships;
				
				if ( rel ) {
					for (const r in rel) {
						var t = rel[r];
						var key = null;
						
						if ( Array.isArray(t) ) {
							key = 0;
						} else {
							t = [t];
						}
						
						for ( target of t ) {
							var targetId = target.id.split(' ')[0];
							var l = r.split(' ')[1];
							
							if ( !items[targetId] ) {
								items[targetId] = cy.add({ group: 'nodes', data: { id: targetId, label: target.id.split(' ')[1] }});
							}
							
							if ( key != null ) {
								if ( target.meta ) {
									key = target.meta.key.split(' ')[1];
									l = l + ' {' + key + '}';
								} else {
									l = l + ' [' + key + ']';
									key = key + 1;
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
			
			  ready: function(){},
			  stop: function(){},
			
			  animate: true,
			
			  animationEasing: undefined,
			  animationDuration: undefined,
			
			  animateFilter: function ( node, i ){ return true; },
			  animationThreshold: 250,
			  refresh: 2,
			  fit: true,
			  padding: 30,
			
			  boundingBox: undefined,
			  nodeDimensionsIncludeLabels: false,
			
			  randomize: false,
			  componentSpacing: 40,
			  nodeRepulsion: function( node ){ return 2048; },
			  nodeOverlap: 4,
			  idealEdgeLength: function( edge ){ return 32; },
			  edgeElasticity: function( edge ){ return 32; },
			  nestingFactor: 1.2,
			  gravity: 1,
			  numIter: 1000,
			  initialTemp: 1000,
			  coolingFactor: 0.99,
			  minTemp: 1.0
			};
			
			var layout = cy.layout( options );
			layout.run();			
		}	
	}
		
	Dust.Graph = new DustGraphInit();
	
	console.log('Dust Cytoscape graph initialized.');
}
