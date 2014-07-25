CQ.form.rte.plugins.NodeDataSelectorPlugin = new Class({

    toString: "Node Data Selector Plugin",

    extend:CQ.form.rte.plugins.Plugin,
    /**
     * @private
     */
    nodeDataSelector: null,

    /**
     * @private
     */
    nodeSelectorText: null,

    /**
     * @private
     */
    envEditContext:null,

    constructor: function(editorKernel) {
        CQ.form.rte.plugins.NodeDataSelectorPlugin.superclass.constructor.call(this,editorKernel);
    },

    callDialog: function(context) {
        var configdialog={
            "insertContentIntoRTE": this.insertContentIntoRTE.createDelegate(this)
        }
        this.nodeDataSelector =new CQ.form.rte.plugins.NodeDataSelector(configdialog);
        this.nodeDataSelector.setPosition(this.editorKernel.calculateWindowPosition("left"));
        /* window.setTimeout(function() {
         //this.nodeDataSelector.toFront();
         //this.nodeDataSelector.focus();
         }.createDelegate(this), 10); */

    },

    getYourData: function(){
        this.nodeSelectorText = "Default Node Data Selector RTE Plugin";
        this.callDialog(this.envEditContext);
    },

    getFeatures: function() {
        return [ "nodedata" ];
    },

    initializeUI: function(tbGenerator) {
        //alert(this.dialog);
        var plg = CQ.form.rte.plugins;
        var ui = CQ.form.rte.ui;
        if (this.isFeatureEnabled("nodedata")) {
            this.checkTextUI = new ui.TbElement("nodedata", this,false,this.getTooltip("nodedata"));
            tbGenerator.addElement("nodeDataSelector", plg.Plugin.SORT_LIST, this.checkTextUI,12);
        }
    },

    notifyPluginConfig: function(pluginConfig) {
        pluginConfig = pluginConfig || { };
        CQ.Util.applyDefaults(pluginConfig, {
            "tooltips": {
                "nodedata": {
                    "title": CQ.I18n.getMessage("Data Selector"),
                    "text":  CQ.I18n.getMessage("Node Data Selector")
                }
            }
        });
        this.config = pluginConfig;
    },

    execute: function(id, value, env) {
        switch (id) {
            case "nodedata":
                this.envEditContext = env.editContext;
                this.getYourData();
                break;
        }
    },

    insertContentIntoRTE: function(value,windowObject) {
        this.editorKernel.execCmd("inserthtml", value);
        windowObject.close();
        this.editorKernel.focus();
    },

    updateState: function(selDef) {}
});

// register plugin
CQ.form.rte.plugins.PluginRegistry.register("nodeDataSelector",CQ.form.rte.plugins.NodeDataSelectorPlugin);
