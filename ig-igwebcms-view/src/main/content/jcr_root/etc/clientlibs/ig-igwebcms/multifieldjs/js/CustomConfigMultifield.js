/**
 * @class CQ.form.CustomConfigMultiField
 * @extends CQ.form.CompositeField
 * The CustomConfigMultiField is an editable list of a set of form fields for editing a list of nodes with their properties. 
 * Unlike the {@link CQ.form.MultiField}, which works on an array of values (multi-value property in JCR), 
 * this widget works on the list of named objects, each containing the same set of properties (nodes with properties in JCR).
 *
 * <p>The naming scheme for the nodes will use a baseName + an automatically incremented number, 
 * e.g. "node_1" where the baseName is "node_". 
 * Note that if ordering is desired (via {@link #orderable}), it will be managed independently from the numbering, only the used node type must support it.
 * Additionally, a prefix can be given for the final field names (just for the submit field names, eg. to support the often required "./" prefix for the Sling POST).
 *
 * @constructor
 * Creates a new CustomConfigMultiField.
 * @param {Object} config The config object
 */
CQ.form.CustomConfigMultiField = CQ.Ext.extend(CQ.form.CompositeField, {

    /**
     * @cfg {Long} maxLimit
     * The maximum limitation for the items that can be added, default value is 2147483647.
     * Optional.
     */
     
    /**
     * @cfg {String} prefix
     * A general prefix added to every field name (eg. for "./") for submit.
     * Optional.
     */
     
    /**
     * @cfg {String} name
     * The container node for the list of managed nodes. If this is set, the individual items to load will be taken from the child nodes
     * of that container node (eg. "container/node_1", "container/node_2"), otherwise from the "current" node, i.e. on the same level as the
     * other fields next to this one (eg. "node_1", "node_2"). In the latter case it is probably desired to set {@link #baseName} and {@link #matchBaseName}
     * to filter out the correct nodes (eg. "baseName_1", "baseName_2").
     * Optional.
     */
     
    /**
     * @cfg {String} baseName
     * A baseName for the node names of the individual objects, eg. "file_". Will
     * be used to create the names for new nodes. If {@link #matchBaseName} is true,
     * it will also be used to filter out the nodes to load. Defaults to "item_".
     */
     
    /**
     * @cfg {Boolean} matchBaseName
     * Whether nodes must match the {@link #baseName} when loading the items.
     * If not, all objects/nodes found are loaded. Defaults to true.
     */
     
    /**
     * @cfg {Array} fieldConfigs
     * An array of configuration options for the fields. Required.
     * <p>Example:
     * <pre><code>
			[{	xtype: "textfield",
			    name: "key",
			    fieldLabel: "Key"
			},{	xtype: "pathfield",
			    name: "value",
			    fieldLabel: "Value"
			}]    
		</code></pre>
     */
    
    /**
     * @cfg {Object} itemPanelConfig
     * A config for the panel that holds the fields defined in {@link #fieldConfigs}.
     * Can be used to define the layout further. The "items" object will be overwritten.
     * Defaults to a simple panel with a from layout.
     */


	// private
    path: "",
    bodyPadding: 0,
    // the width of the field     
    fieldWidth: 0,
    
    
    constructor: function(config) {
        var list = this;

        var items = new Array();
        
        if (!config.addItemLabel) {
            config.addItemLabel = CQ.I18n.getMessage("Add Item");
        }

        if(!config.readOnly) {
            items.push({
                xtype: "toolbar",
                cls: "cq-multifield-toolbar",
                items: [
                    "->", {
                        xtype: "textbutton",
                        text: config.addItemLabel,
                        style: "padding-right:6px",
                        handler:function() {
                            list.addItem();
                            list.doLayout();
                        }
                    }, {
                        xtype: "button",
                        iconCls: "cq-multifield-add",
                        template: new CQ.Ext.Template('<span><button class="x-btn" type="{0}"></button></span>'),
                        handler: function() {
                            list.addItem();
                            list.doLayout();
                        }
                    }
                ]
            });
        }

        if (config.name) {
            this.hiddenDeleteField = new CQ.Ext.form.Hidden({
                "name":config.name + CQ.Sling.DELETE_SUFFIX
            });
            items.push(this.hiddenDeleteField);
        }

        config = CQ.Util.applyDefaults(config, {
            fieldConfigs: [],
            itemPanelConfig: {
                xtype: "panel",
                layout: "form",
                border: false
            },
            orderable: true,
            baseName: "item_",
            matchBaseName: true,
            border: true,
            maxLimit:2147483647,
            items:[{
                xtype: "panel",
                border:false,
                bodyStyle: "padding:4px;",
                items: items
            }]
        });
        CQ.form.CustomConfigMultiField.superclass.constructor.call(this,config);

        // typical example: prefix="./", name="items" => "./items/"
        this.fieldNamePrefix = config.prefix || "";
        if (config.name) {
            this.fieldNamePrefix += config.name + "/";
        }
    },

    initComponent: function() {
        CQ.form.CustomConfigMultiField.superclass.initComponent.call(this);
    },


    /**
     * Creates the name for a new field. Must take the baseName and append unique number
     */
    createName: function() {
        for (var i = 1;; i++) {
            var name = this.baseName + i;
            
            // check if this name has been used
            var item = this.items.find(function(item) {
                return item.name == name;
            });
            if (!item) {
                return name;
            }
        }
        return "";
    },

    /**
     * Adds a new field with the specified value to the list.
     * @param {String} name name of the object to add
     * @param {Object} o The object to add
     */
    addItem: function(name, o) {
 
	    if (this.maxLimit > 0 && this.items.getCount() > this.maxLimit) {
	        alert("Items have reached the maximum: "+this.maxLimit);
	        return;
	    }
	    
        if (!name) {
            // new item to add
            name = this.createName();
        }
        
        // What to do with values that couldn't be found? we delete the nodes normally...
        var item = this.insert(this.items.getCount()-1, {
            xtype: "customconfigmultifielditem",
            name: name,
            prefix: this.fieldNamePrefix,
            orderable: this.orderable,
            readOnly: this.readOnly,
            fieldConfigs: this.fieldConfigs,
            panelConfig: this.itemPanelConfig
        });

        item.processPath(this.path);
        if (o) {
            item.setValue(o);
        }
        
        this.doLayout();
    },
    
    processPath: function(path) {
        this.path = path;
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        var value = new Array();
        this.items.each(function(item, index) {
            if (item instanceof CQ.form.CustomConfigMultiField.Item) {
                value[index] = item.getValue();
                index++;
            }
        }, this);
        return value;
    },

    // private, loads a single object
    processItem: function(name, o) {
        if (typeof o !== "object") {
            return;
        }
        
        if (this.baseName && this.matchBaseName !== false) {
            // check if o.name starts with the baseName
            if (name.indexOf(this.baseName) !== 0) {
                return;
            }
        }
        this.addItem(name, o);
    },
    
    // overriding CQ.form.CompositeField#processRecord
    processRecord: function(record, path) {
        
        if (this.fireEvent('beforeloadcontent', this, record, path) !== false) {
            
            // remove all existing fields
            this.items.each(function(item) {
                if (item instanceof CQ.form.CustomConfigMultiField.Item) {
                    this.remove(item, true);
                }
            }, this);
            
            if (this.name) {
                var c = record.get(this.name);
                for (var n in c) {
                    var v = record.get(this.getName());
                    this.processItem(n, c[n]);
                }
            } else {
                record.fields.each(function(field) {
                    this.processItem(field.name, record.get(field.name));
                }, this);
            }
            this.doLayout();
            this.fireEvent('loadcontent', this, record, path);
        }
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
    },

    // private
    afterRender : function(){
        CQ.form.CustomConfigMultiField.superclass.afterRender.call(this);
        this.doLayout();
    }

});

CQ.Ext.reg("customconfigmultifield", CQ.form.CustomConfigMultiField);


/**
 * @private
 * @class CQ.form.CustomConfigMultiField.Item
 * @extends CQ.Ext.Panel
 * The CustomConfigMultiField.Item is an item in the {@link CQ.form.CustomConfigMultiField}.
 * This class is not intended for direct use.
 * @constructor
 * Creates a new CustomConfigMultiField.Item.
 * @param {Object} config The config object
 */
CQ.form.CustomConfigMultiField.Item = CQ.Ext.extend(CQ.Ext.Panel, {

    /**
     * @cfg {String} name
     * @member CQ.form.CustomConfigMultiField.Item
     * Name of this item.
     */
    /**
     * @cfg {String} prefix
     * @member CQ.form.CustomConfigMultiField.Item
     * Prefix to add to all field names.
     */
    /**
     * @cfg {Boolean} readOnly
     * @member CQ.form.CustomConfigMultiField.Item
     * If the fields should be read only.
     */
    /**
     * @cfg {Array} fieldConfigs
     * @member CQ.form.CustomConfigMultiField.Item
     * Array of field configurations.
     */
    /**
     * @cfg {Object} panelConfig
     * @member CQ.form.CustomConfigMultiField.Item
     * Config for panel holding fields.
     */
     
    constructor: function(config) {
        var item = this;
        var fields = CQ.Util.copyObject(config.fieldConfigs);
        for (var i = 0; i < fields.length; i++) {
            var f = fields[i];
            f.rawFieldName = f.name;
            f.name = config.prefix + config.name + "/" + f.rawFieldName;
            f.readOnly = config.readOnly;
        }

        config.panelConfig = CQ.Util.copyObject(config.panelConfig);
        config.panelConfig.items = fields;
        config.panelConfig.cellCls = "cq-multifield-itemct";
        config.panelConfig.border = true;
        
        var items = new Array();
        items.push(config.panelConfig);

        if(!config.readOnly) {
            if (config.orderable) {
                items.push({
                    xtype: "panel",
                    border: false,
                    items: {
                        xtype: "button",
                        "iconCls": "cq-multifield-up",
                        "template": new CQ.Ext.Template('<span><button class="x-btn" type="{0}"></button></span>'),
                        handler: function(){
                            var parent = item.ownerCt;
                            var index = parent.items.indexOf(item);
                            
                            if (index > 0) {
                                item.reorder(parent.items.itemAt(index - 1));
                            }
                        }
                    }
                });
                items.push({
                    xtype: "panel",
                    border: false,
                    items: {
                        xtype: "button",
                        "iconCls": "cq-multifield-down",
                        "template": new CQ.Ext.Template('<span><button class="x-btn" type="{0}"></button></span>'),
                        handler: function(){
                            var parent = item.ownerCt;
                            var index = parent.items.indexOf(item);
                            // note: there is one last item for the add button, must be ignored
                            if (index < parent.items.getCount() - 2) {
                                parent.items.itemAt(index + 1).reorder(item);
                            }
                        }
                    }
                });
            }
            items.push({
                xtype: "panel",
                border: false,          
                items: {
                    xtype: "button",
                    "iconCls": "cq-multifield-remove",
                    "template": new CQ.Ext.Template('<span><button class="x-btn" type="{0}"></button></span>'),
                    handler: function() {
                        item.ownerCt.remove(item);
                    }
                }
            });
        }

        config = CQ.Util.applyDefaults(config, {
            "layout": "table",
            "anchor": "100%",
            "bodyCssClass": "cq-multifield-item",
            "border": false,
            "style" : "margin-bottom:-3px",
            "layoutConfig": {
                "columns": 4
            },
            "defaults": {
                "bodyStyle": "padding:5px; border-top-width:0px; border-bottom-width:0px; border-left-width:0px;"
            },
            "items": items
        });
        CQ.form.CustomConfigMultiField.Item.superclass.constructor.call(this, config);
        
        this.fields = new CQ.Ext.util.MixedCollection(false, function(field) {
            return field.rawFieldName;
        });
        this.getFieldPanel().items.each(function(item) {
            if (item.rawFieldName) {
                this.fields.add(item.rawFieldName, item);
            }
        }, this);

        if (config.value) {
            this.setValue(config.value);
        }
    },

    getFieldPanel: function() {
        return this.items.get(0);
    },

    setPanelWidth: function(w) {
        this.getFieldPanel().setWidth(w);
    },

    /**
     * Reorders the item above the specified item.
     * @param item {CQ.form.CustomConfigMultiField.Item} The item to reorder above
     * @member CQ.form.CustomConfigMultiField.Item
     */
    reorder: function(item) {
        var c = this.ownerCt;
        // move this item before the other one in the parent
        c.insert(c.items.indexOf(item), this);
        // must manually move dom element as well
        this.getEl().insertBefore(item.getEl());
        c.doLayout();
    },
    
    processPath: function(path) {
        this.fields.each(function(f) {
            if (f.processPath) {
                f.processPath(path);
            }
        });
    },
    
    /**
     * Returns the data value.
     * @return {Object} value The field value
     * @member CQ.form.CustomConfigMultiField.Item
     */
    getValue: function() {
        var o = {};
        this.fields.each(function(f) {
            o[f.rawFieldName] = f.getValue();
        });
        return o;
    },

    /**
     * Sets a data value into the field and validates it.
     * @param {Object} value The value object to set
     * @member CQ.form.CustomConfigMultiField.Item
     */
    setValue: function(value) {
        this.fields.each(function(f) {
            if (value[f.rawFieldName]) {
                f.setValue(value[f.rawFieldName]);
            }
        });
    }
});

CQ.Ext.reg("customconfigmultifielditem", CQ.form.CustomConfigMultiField.Item);