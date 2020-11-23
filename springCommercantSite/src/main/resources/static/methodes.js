
    function adjustBody() {

        console.log('adjustBody ');

        var h = $('header').height();
        if ($('header').hasClass("fixed-top")) {
            $('body').css("padding-top", h);
        } else {
            $('body').removeClass("padding-top");
        }
    }

    function addQte(btn) {
        let parent = btn.parentElement.parentElement.parentElement;

        let cible = parent.querySelector(".qte");

        if (cible != null) {
            let qte = cible.value;

            if (qte == null) {
                qte = 1;
            }
            cible.value = parseFloat(qte) + 1;
            cible.setAttribute('value', parseFloat(qte) + 1);

            updateQuantity(cible);
        }
    }
    function dimQte(btn) {
        let parent = btn.parentElement.parentElement.parentElement;
        let cible = parent.querySelector(".qte");

        if (cible != null) {
            let qte = cible.value;
            if (qte == null) {
                qte = 1;
            }
            if (qte > 0) {
                cible.value = parseFloat(qte) - 1;
                cible.setAttribute('value', parseFloat(qte) - 1);
            } else {
                cible.value = 0;
                cible.setAttribute('value', 0);
            }


            updateQuantity(cible);
        }

    }

    function supprimerProd(btn) {
        let parentTR = btn.closest("tr");
        if (parentTR != null) {
            supprimerProdAjax(parentTR);
        }
    }

    function supprimerProdAjax(parent) {
        var id = parseInt(parent.querySelector(".idProd").value);
        data = { id: id };
        urlTo = "/removeProduitPanier";
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: urlTo,
            data: JSON.stringify(data),
            dataType: 'json',
            async: false,
            success: function (result, textStatus, xhr) {
                if (xhr.status == 200) {
                    parent.remove();//removeProduitPanier
                    recalculateCart();
                    var res = parseInt(result);
                    updateNavBarPanier(res);
                } else {
                    console.log('Erreur status ' + xhr.status);
                }
            },
            error: function (e) {
                console.log("ERROR: ", e);
            }
        });
    }

    function updateQuantity(quantityInput) {

        var parent = quantityInput.closest("tr");

        var id = parseInt(parent.querySelector(".idProd").value);

        var price = parseFloat(parent.querySelector(".prix").textContent);

        var quantity = parseFloat(parent.querySelector(".qte").value);
        var linePrice = getProdTotalAjax(id, price, quantity, parent);
    }


    function getProdTotalAjax(id, prix, qte, parent) {
        data = { id: id, prix: prix, qte: qte };
        urlTo = "/setProduitPanier";
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: urlTo,
            data: JSON.stringify(data),
            dataType: 'json',
            async: false,
            success: function (result, textStatus, xhr) {
                if (xhr.status == 200) {

                    var result = parseFloat(result).toFixed(2);
                    var subTotalElt = parent.querySelector(".subtotal");
                    var subTotalEltVisible = parent.querySelector(".subtotalV");
                    subTotalElt.textContent = result;
                    subTotalEltVisible.textContent = result + '€';
                    recalculateCart();

                } else {
                    console.log('Erreur status ' + xhr.status);
                }
            },
            error: function (e) {
                console.log("ERROR: ", e);
            }
        });
    }

    function recalculateCart() {
        recalculateCartAjax();
    }

    /* test AJAX*/
    function recalculateCartAjax() {

        products = [];
        var allByClass = document.getElementsByClassName('item');
        for (var i = 0, len = allByClass.length | 0; i < len; i = i + 1 | 0) {
            var id = parseInt(allByClass[i].querySelector(".idProd").value);
            var price = parseFloat(allByClass[i].querySelector(".prix").textContent);
            var quantity = parseFloat(allByClass[i].querySelector(".qte").value);
            data = { id: id, prix: price, qte: quantity };
            products.push(data);
        }

        var formData = { produits: products };
        urlTo = "/getTotalPanier";

        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: urlTo,
            data: JSON.stringify(formData),
            dataType: 'json',
            async: false,
            success: function (result, textStatus, xhr) {
                if (xhr.status == 200) {
                    var result = parseFloat(result).toFixed(2);
                    var totalElt = document.getElementById('totalPanier');
                    totalElt.textContent = 'Total : ' + result + '€';
                } else {
                    console.log('Erreur status ' + xhr.status);
                }
            },
            error: function (e) {
                console.log("ERROR: ", e);
            }
        });
    }
    
	function addToPanierExpress(btn, id){
		console.log('addToPanierExpress ' + id);				
		data = {id : id, qte : 1};
		urlTo = "/addProdExpress";						
		$.ajax({type : "POST",
					contentType : "application/json",
					url : urlTo,
					data : JSON.stringify(data),
					dataType : 'json',
					async : false,
					success : function(result, textStatus, xhr) {
						if (xhr.status == 200) {
							
							var res = parseInt(result);
							updateNavBarPanier(res);
							//let txtPanier='Panier (' + result + ')';							
							//$('#linkPanierPlein').removeClass('d-none'); 
							//$('#linkPanierPlein span').text(txtPanier);
							//$('#linkPanierVide').addClass('d-none');						
							
						} else {
							console.log('Erreur status ' + xhr.status);
						}
					},
					error : function(e) {
						console.log("ERROR: ");
						console.log(e);
					}
		});
	}
			
    function retrievePanierFragment(idTag) {
        var url = '/panierFragment';
        console.log(url);        
		$(idTag).empty();
        $(idTag).load(url, function() {       	
  			 $(idTag).find('#dlgPanier').modal('show');
  			// $('#dlgPanier').modal('show');
		});      
       
        
    }
    
    function updateNavBarPanier(result) {
	    if(result>0){
	    	let txtPanier='Panier (' + result + ')';							
			$('#linkPanierPlein').removeClass('d-none'); 
			$('#linkPanierPlein span').text(txtPanier);
			$('#linkPanierVide').addClass('d-none');
	    }else {
	    	$('#linkPanierVide').removeClass('d-none'); 
			$('#linkPanierPlein span').text('Panier (0)');
			$('#linkPanierPlein').addClass('d-none');
	    }
	       
    }
