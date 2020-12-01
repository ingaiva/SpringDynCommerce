
    
    function addQteCmd(btn) {
    console.log('addQteCmd');
		let parent = btn.parentElement.parentElement.parentElement;
		let cible = parent.querySelector(".qteCmd");
		if (cible != null) {
			let qte = cible.value;

			if (qte == null) {
				qte = 1;
			}
			cible.value = parseFloat(qte) + 1;
			updateQuantityCmd(cible);
		}
	}
			
	function dimQteCmd(btn) {
		let parent = btn.parentElement.parentElement.parentElement;
		let cible = parent.querySelector(".qteCmd");

		if (cible != null) {
			let qte = cible.value;
			if (qte == null) {
				qte = 1;
			}
			if (qte > 0) {
				cible.value = parseFloat(qte) - 1;
			} else
				cible.value = 0;
			
			updateQuantityCmd(cible);
		}

	}

	function supprimerProdCmd(btn) {
		let parentTR = btn.closest("tr");
		if (parentTR != null) {
			parentTR.remove();
			recalculateCartCmd();
		}
	}			

	function updateQuantityCmd(quantityInput) {
		   console.log('updateQuantityCmd');
		var parent = quantityInput.closest("tr");
		
		var price = parseFloat(parent.querySelector(".prixCmd").textContent);			
		
		var quantity = parseFloat(parent.querySelector(".qteCmd").value);
		
		var id=parseInt(parent.querySelector(".idProdCmd").value);
		
		getProdTotalAjax(id,price,quantity, parent);
		
	}

	function getProdTotalAjax(id, prix, qte, parent) {
		data={produit:{id : id, prix : prix}, qte: qte};				

		urlTo = "/getTotalProduitCmd";		
		console.log(urlTo);
		
		$.ajax({type : "POST",
					contentType : "application/json",
					url : urlTo,
					data : JSON.stringify(data),
					dataType : 'json',
					async : false,
					success : function(result, textStatus, xhr) {
						if (xhr.status == 200) {

							var result = parseFloat(result).toFixed(2);
							var subTotalElt = parent.querySelector(".subtotalCmd");
							var subTotalEltVisible = parent.querySelector(".subtotalCmdV");
							subTotalElt.textContent = result;
							subTotalEltVisible.textContent = result	+ '€';
							recalculateCartCmd();

						} else {
							console.log('Erreur status ' + xhr.status);
						}
					},
					error : function(e) {
						console.log("ERROR: ", e);
					}
		});
	}
	
	
	/* Recalculate cart */
	function recalculateCartCmd() {	
		recalculateCartCmdAjax();
	}
	
	
	function recalculateCartCmdAjax() {
			//Methode differente du site commercial!!!
			statutCmd=document.getElementById('statutCmd').value;//
			idCmd=document.getElementById('id').value;//
			reductionSpeciale=document.getElementById('reductionS').value;//
			
			lignesProd = [];
			var allByClass = document.getElementsByClassName('itemCmd');
			for (var i = 0, len = allByClass.length | 0; i < len; i = i + 1 | 0) {
				var id = parseInt(allByClass[i].querySelector(".idProdCmd").value);
				var price = parseFloat(allByClass[i].querySelector(".prixCmd").textContent);
				var quantity = parseFloat(allByClass[i].querySelector(".qteCmd").value);
				data = {produit:{id : id, prix : price}, qte: quantity};		
				//console.log(JSON.stringify(data));
				lignesProd.push(data); 
			}	
			
			var formData =  {id:idCmd,lignesCommandeProduit : lignesProd, statut: statutCmd, reductionSpeciale:reductionSpeciale};								
			
			urlTo = "/calculeTotalCmd";				
		
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : urlTo,
				data : JSON.stringify(formData),
				dataType : 'json',
				async : false,
				success : function(result, textStatus, xhr) {
					if (xhr.status == 200) {
										
						var res = parseFloat(result.totalFinal).toFixed(2);	
						var resProd = parseFloat(result.totalSansPromo).toFixed(2);							
						var totalElt = document.getElementById('totalCmd');
						var totalProdElt=document.getElementById('totalProdCmd');//
						totalElt.textContent = 'Total : ' + res + '€';
						totalProdElt.textContent = 'Total produit : ' + resProd + '€';
					} else {
						console.log('Erreur status ' + xhr.status);
					}
				},
				error : function(e) {
					console.log("ERROR: ", e);
				}
			});
		} 
		
		
		$('.lblPtV').popover({
            html: true,
            content: function () {
                var elementId = $(this).attr("data-popover-content");
                
                return $(elementId).html();
            }
        });
