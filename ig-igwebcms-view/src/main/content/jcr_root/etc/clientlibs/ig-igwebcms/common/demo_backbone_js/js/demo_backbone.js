$(function(){
	//Creating model with name Service.
	var Service = Backbone.Model.extend({

	defaults:{
		title : 'Test Title' ,
		price : 100,
		checked : false
	},
	toggle: function(){
		this.set('checked' ,!this.get('checked'));
	}
	});

	//Creating collection of Services in which model is Service and have a function getChecked().
	var ServiceList = Backbone.Collection.extend({
		model: Service,

		getChecked: function(){
			return this.where({checked:true});		
		}	
	});

    //Creating a Collection in which data is fetched from server from given URL .And response is parsed.
    var ServerDataCollection = Backbone.Collection.extend({
        model: Service ,
        url : "http://localhost:4502/ajax_test/test",
        parse :function(response)
        {
            return response.list;
        },
        getChecked: function(){
            return this.where({checked:true});
        }
    });

	var dataCollection = new ServerDataCollection();

    //Fetching the data from server and setting Asynchrous as FALSE.
    dataCollection.fetch({async:false});

	//Creating a collection of Services model defined above
	var services = new ServiceList([
		new Service({title : 'Coffee' ,price : 200 }),
		new Service({title : 'Tea' ,price : 250}),
		new Service({title : 'Snacks' ,price : 100}),
		new Service({title : 'Combo' ,price : 2000})		
	]);


    //This is view for indivisual checkbox and it has a MODEL assocoated with it. 
	var ServiceView = Backbone.View.extend({
	
		tagName: 'li' ,

        //Registering the event CLICK to call the method "toggleService"(Defined below) on each CLICK.
		events:{
			'click' : 'toggleService'		
		},
		initialize: function(){

            //Registering this View to listen for changes in the model associated with this view . And on change event it will fire
            //Render method.
			this.listenTo(this.model ,'change' ,this.render);
		},

		render: function(){
			//Render method to render the View.
			this.$el.html('<input type="checkbox" name="'+this.model.get('title')+'" />'+this.model.get('title')+'<span>  ::Price:: Rs '+
            this.model.get('price') + '</span>');
			this.$('input').prop('checked' ,this.model.get('checked'));
			return this;
		},
		
		toggleService:function(){
			this.model.toggle();
		}	

	});

    //This is the Final view for complete App. This will render the Other views in itself(ServiceView)
	var App = Backbone.View.extend({
		//This is el variable which is assocoiated with each view. And here we are setting it to a div with class main
        el :$('.main') ,

        //Initialize method is called on creating this View.
		initialize :function(){
			this.total = $('.total span');
			this.container = $('.services');

            //Registering this View to listen for change in collection : dataCollection and calling Render function on change
			this.listenTo(dataCollection, 'change', this.render);

			dataCollection.each(function(service){
				var view = new ServiceView({model : service});
                //Appending each Service view in the container UL
				this.container.append(view.render().el);
			},this);

		},

		render : function(){
			var total=0;
			_.each(dataCollection.getChecked() ,function(elem){
				total+=elem.get('price');				
			});		
			
			this.total.text('Rs->'+total);
						
			return this;
		}
	});

    //Creating the Actual view of the Application.
	new App();	
});