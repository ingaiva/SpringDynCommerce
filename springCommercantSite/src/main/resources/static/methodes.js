$(window).scroll(function () {
    var s = parseInt($(document).scrollTop());
    //console.log('on scroll: ' + ' doc :'+ $( document ).height() + ' w ' + $(window).height());	
    var h1 = $('#divNavbarUser').height() + parseInt($("#divNavbarUser").css("marginBottom")) + parseInt($("#divNavbarUser").css("marginTop"));
    var h2 = $('#divNavbarSiteCommercant').height() + parseInt($("#divNavbarSiteCommercant").css("marginBottom")) + parseInt($("#divNavbarSiteCommercant").css("marginTop"));
    isTooBig = ($(document).height() - $("#divLogoSite").height()) > $(window).height();
    if (isTooBig) {
   
   //console.log('on scroll: ' + (h1 +  h2) + '/s:'+ $(document).scrollTop() );
       
        if ($(document).scrollTop() > 0) {

            if (!($('#divLogoSite').hasClass("shrink"))) {
				let hasToScrollToTop=((h1 + h2) > s);
				

               // console.log("pas de class et "+ hasToScrollToTop);

                $("#divLogoSite").fadeOut("slow", function () {
                    //console.log('addClass shrink ' + $(document).scrollTop());
                    $('#divLogoSite').addClass('shrink');//
                   if((h1 + h2) > parseInt($(document).scrollTop())){
                   		//console.log('on y va ' + $(document).scrollTop());
                   		$(document).scrollTop((h1 + h2));
                   }

                    //console.log(" hasClass shrink " + ($('#divLogoSite').hasClass("shrink")));
                    // console.log('on scrolltop: ' + (h1 +  h2) + '/s:'+ s + ' w:'+$(window).height());	
                   // if (hasToScrollToTop) {
                    	//console.log('on vas remonter');
                       // $(document).scrollTop(1);
                        //console.log('scrollTop(1) ');	
                   // }
                });
            }
        } else {
            if ($('#divLogoSite').hasClass("shrink")) {
               // console.log('removeClass shrink ');
                $('#divLogoSite').removeClass('shrink');
                $("#divLogoSite").fadeIn("slow");
                //$( "#divLogoSite" ).fadeIn( "slow", function() {    	
                //    $('#divLogoSite').removeClass('shrink');
                //});  	
            }
        }

    }
});

    function adjustBody() {

        var h = $('header').height();
        if ($('header').hasClass("fixed-top")) {
            $('body').css("padding-top", h);
        } else {
           // $('body').removeClass("padding-top");
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
                    var res = parseFloat(result).toFixed(2);
                    var totalElt = document.getElementById('totalPanier');
                    totalElt.textContent = 'Total : ' + res + '€';
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
	
	function getPanierNombreProd(){		
		urlTo = "/getPanierNombreProd";						
		$.ajax({type : "GET",					
			url : urlTo,			
			
			success : function(result, textStatus, xhr) {
				if (xhr.status == 200) {							
					var res = parseInt(result);
					updateNavBarPanier(res);												
					
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
	
	
	function retrievePointVenteFragment(tag , idLoadTag){
	 	var parent = tag.closest("tr");
        var id = parseInt(parent.querySelector(".idPTV").value);
        data = {id : id};
		var url = '/pointVenteFragment' + '/' + id;
		
		console.log(url);
		
		$(idLoadTag).empty();
        $(idLoadTag).load(url, 
                  function() {
              $(idLoadTag).find('.modal').modal('show');  //modal     
  			 //$(idLoadTag).find('#dlgPointVente').modal('show');  //modal			
		});
	}		
			
    function retrievePanierFragment(idTag) {
        var url = '/panierFragment';
               
		$(idTag).empty();
        $(idTag).load(url, function() {  
        	$(idTag).find('#dlgPanier').on('hide.bs.modal', function (e) {
  				 
  				getPanierNombreProd();
			})
        
             	
  			 $(idTag).find('#dlgPanier').modal('show');
  			
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
    
    
    function addQteCmd(btn) {
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
		  
		var parent = quantityInput.closest("tr");
		
		var price = parseFloat(parent.querySelector(".prixCmd").textContent);			
		
		var quantity = parseFloat(parent.querySelector(".qteCmd").value);
		
		var id=parseInt(parent.querySelector(".idProdCmd").value);
		
		getProdTotalCmdAjax(id,price,quantity, parent);
		
	}

	function getProdTotalCmdAjax(id, prix, qte, parent) {
		//data={produit:{id : id, prix : prix}, qte: qte, prix : prix};	
					
		data={produit:{id : id}, qte: qte, prix : prix};
		urlTo = "/getTotalProduitCmd";		
		
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
				
				statutCmd=document.getElementById('statutCmd').value;//
				idCmd=document.getElementById('id').value;//
								
				lignesProd = [];
				var allByClass = document.getElementsByClassName('itemCmd');
				for (var i = 0, len = allByClass.length | 0; i < len; i = i + 1 | 0) {
					var id = parseInt(allByClass[i].querySelector(".idProdCmd").value);
					var price = parseFloat(allByClass[i].querySelector(".prixCmd").textContent);
					var quantity = parseFloat(allByClass[i].querySelector(".qteCmd").value);
					//data = {produit:{id : id, prix : price}, qte: quantity};		
					data = {produit:{id : id}, qte: quantity, prix : price};
					lignesProd.push(data); 
				}	
				
				var formData =  {id:idCmd,lignesCommandeProduit : lignesProd, statut: statutCmd};								
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
							updateUiTotauxCmd(result);
							//var res = parseFloat(result.totalFinal).toFixed(2);	
							//var resProd = parseFloat(result.totalSansPromo).toFixed(2);						
							//var totalElt = document.getElementById('totalCmd');
							//var totalProdElt=document.getElementById('totalProdCmd');//
							//totalElt.textContent = 'Total : ' + res + '€';
							//if(totalProdElt !=null){
								//totalProdElt.textContent = 'Total produit : ' + resProd + '€';
							//}
						} else {
							console.log('Erreur status ' + xhr.status);
						}
					},
					error : function(e) {
						console.log("ERROR: ", e);
					}
				});
			} 
			
			function updateUiTotauxCmd(result) {
				var res = parseFloat(result.totalFinal).toFixed(2);	
				var resProd = parseFloat(result.totalSansPromo).toFixed(2);						
				var totalElt = document.getElementById('totalCmd');
				var totalProdElt=document.getElementById('totalProdCmd');//
				totalElt.textContent = 'Total : ' + res + '€';
				if(totalProdElt !=null){
					totalProdElt.textContent = 'Total produit : ' + resProd + '€';
				}
			}
