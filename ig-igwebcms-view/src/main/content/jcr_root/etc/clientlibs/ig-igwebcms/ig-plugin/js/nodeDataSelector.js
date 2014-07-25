CQ.form.rte.plugins.NodeDataSelector = CQ.Ext.extend(CQ.Ext.Window, {

    insertContentIntoRTE: null,
    title: "Default Data Selector",
    width: 500,
    height: 300,
    minWidth: 400,
    minHeight: 200,
    buttonItems: [],
    key: '',
    value: '',
    path: '',
    constructor: function (config) {

        this.insertContentIntoRTE = config.insertContentIntoRTE;

        var colModel = new CQ.Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                sortable: true
            },
            columns: [
                {header: 'Key', dataIndex: 'key'},
                {header: 'Value', dataIndex: 'value'}
            ]
        });

        // Tree loader
        var loader = new CQ.Ext.tree.TreeLoader({
            "url": CQ.HTTP.externalize("/etc/rates.ext.json"),
            "requestMethod": "GET",
            "baseParams": { "predicate": "hierarchy", "depth": 0 },
            "baseAttrs": { "iconCls": "page" }
        });
        loader.on("beforeload", function (loader, node) {
            try {
                var path = node.getPath();
                if (path == "/content") {
                    path = "/etc/rates";
                }
                path = path.replace("content", "etc/rates");
                this.url = CQ.HTTP.externalize(path + ".ext.json");
            } catch (e) {
                alert(e);
            }
        });

        // Tree root
        var root = new CQ.Ext.tree.AsyncTreeNode({
            "name": "content",
            "text": CQ.I18n.getMessage("Pages"),
            "expanded": false,
            "iconCls": "page"
        });

        // Tree panel
        var tree = new CQ.Ext.tree.TreePanel({
            "id": "cq-paragraphreference-tree",
            "xtype": "treepanel",
            "region": "west",
            "width": "50%",
            "height": this.height - 62,
            "loader": loader,
            "root": root,
            "autoScroll": true
        });
        tree.on("click", this.onSelectPage.createDelegate(this));

        // data store
        var reader = new CQ.Ext.data.JsonReader({
            "fields": [ "key", "value", "path" ]
        });
        this.proxy = new CQ.Ext.data.HttpProxy({ "url": "/" });
        this.store = new CQ.Ext.data.Store({
            "proxy": this.proxy,
            "reader": reader,
            "autoLoad": false
        });

        // data view
        this.data = new CQ.Ext.grid.GridPanel({
            id: 'resultGrid',
            store: this.store,
            colModel: colModel,
            viewConfig: {
                forceFit: true,
                autoFill: false
            },
            sm: new CQ.Ext.grid.RowSelectionModel({singleSelect: true,
                listeners: {
                    rowselect: function (sm, row, record) {
                        key = record.get('key');
                        value = record.get('value');
                        path = record.get('path');
                    }
                }
            }),
            width: 200,
            height: 150,
            frame: true
        });

        this.selectButton = new CQ.Ext.Button({
            "text": CQ.I18n.getMessage("Select"),
            "handler": this.onSelect.createDelegate(this)
        });

        this.cancelButton = new CQ.Ext.Button({
            "text": CQ.I18n.getMessage("Cancel"),
            "handler": this.onCancel.createDelegate(this)
        });

        this.dialog = new CQ.Ext.Window({
            "id": "resultDialog",
            "title": CQ.I18n.getMessage(this.title),
            "width": this.width,
            "height": this.height,
            "minWidth": this.minWidth,
            "minHeight": this.minHeight,
            "closable": true,
            "closeAction": "destroy",
            "stateful": false,
            "modal": true,
            items: [tree, this.data],
            "buttons": {
                "width": 25,
                "height": 25,
                "border": false,
                items: [ this.selectButton, this.cancelButton ]
            }
        });

        this.dialog.show();
        this.dialog.focus();
    },

    onSelect: function () {
        var fn = this.insertContentIntoRTE;
        if (fn) {
            fn("@{" + path + ".value}", this.dialog);
        }
    },

    onSelectPage: function (node, event) {
        try {
            var path = node.getPath();
            path = path.replace("content", "etc/rates");
            this.proxy.api["read"].url = CQ.HTTP.externalize(path + ".nodedata.json", true);
            this.store.reload();
        } catch (e) {
            alert(e);
        }
    },

    onCancel: function () {
        console.log(this.dialog.parent);
        this.dialog.close();
    }

});
