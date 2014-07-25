
CQ.field = CQ.Ext.extend(CQ.Ext.form.Panel, {
    title: 'Select a Page',
    width:400,
    constructor: function (config) {

    }

});

CQ.wcm.msm.CloudwordsSource = CQ.Ext.extend(CQ.Ext.Panel, {
    /**
     * @cfg {String} url
     * URL to retrieve the CloudwordsSource store.
     */
    url: null,

    /**
     * @cfg {String} rootPath
     * Path of the Blueprint
     */
    rootPath: null,

    contextMenus: [],

    constructor: function(config) {
        config = (!config ? {} : config);
        config = CQ.Util.applyDefaults(config, {
            "url":"/libs/wcm/msm/content/commands/blueprintstatus.json",
            "rootPath":"/content",
            "defaultLCFilter":""
        });

        this.url = config.url;
        this.rootPath = config.rootPath;
        this.lcMaxColumns = CQ.wcm.msm.CloudwordsSource.DEFAULT_MAX_VISIBLE_COLUMNS;
        this.headerStore = this.getHeaderStore(this.url, this.rootPath, config.defaultLCFilter);
        this.treeContainer = new CQ.Ext.Panel({
            "region": "west",
            "split": true,
            "layout":"fit",
            "collapsible": true,
            "collapseMode":"mini",
            "animate": true,
            "hideCollapseTool": true,
            "border": false,
            "width": 350
        });

        this.gridContainer = new CQ.Ext.Panel({
            "region": "center",
            "layout": "fit",
            "border": false,
            "margins": "0 5 0 5"
        });

        var defaults = {
            "layout": "border",
            "height": 600,
            "width": 1100,
            //default tree config
            "tree": {
                "lines": true,
                "border": false,
                "borderWidth": CQ.Ext.isBorderBox ? 0 : 2, // the combined left/right border for each cell
                "cls": "x-column-tree",
                "stateful":false,
                "rootVisible": false,
                "autoScroll":true
            },
            "grid": {
                "border": false
            },
            "items": [

            ]
        };

        CQ.Util.applyDefaults(config, defaults);

        // init component by calling super constructor
        CQ.wcm.msm.CloudwordsSource.superclass.constructor.call(this, config);
    },

    hideContextMenu: function() {
        for (var i = 0; i < this.contextMenus.length; i++) {
            this.contextMenus[i].data = null;
            this.contextMenus[i].widget = null;
            this.contextMenus[i].hide();
        }
    },

    showContextMenu: function(position, data) {
        this.hideContextMenu();
        //try to find a common set of actions for the menu, i.e. selected cells have common properties
        var lcpage = 1;
        var lcheader = 2;
        var bp = 4;
        var menuMask = 0;
        for(var i=0;i<data.length;i++) {
            if (data[i]["lr"]) {
                menuMask |= lcpage;
            } else {
                if (data[i]["lcPath"]) {
                    menuMask |= lcheader;
                } else {
                    menuMask |= bp;
                }
            }
        }
        var menu = null;
        if (menuMask == lcpage) {
            menu = this.getLCPageContextMenu();
        } else if (menuMask == lcheader) {
            menu = this.getLCHeaderContextMenu();
        } else if (menuMask == bp) {
            menu = this.getBPContextMenu();
        }

        if (menu) {
            menu["data"] = data;
            menu["widget"] = this;
            menu.showAt(position);
        }
    },

    getHeaderStore: function(url, rootPath, filter) {
        url = CQ.HTTP.addParameter(url, "headers", true);
        url = CQ.HTTP.addParameter(url, "rootPath", rootPath);
        url = CQ.HTTP.addParameter(url, "filter", filter || "");
        return new CQ.Ext.data.JsonStore({
            "proxy": new CQ.Ext.data.HttpProxy({
                "url": url
            }),
            "fields":["path", "name"]
        });
    },

    /**
     * Builds the browsing tree
     * @private
     */
    getTree: function(gridStore) {
        var columns = new Array();
        columns.push(CQ.Util.applyDefaults({
            "dataIndex": "page",
            "width": 300,
            "renderer": function(nodeData, node, data) {
                var html = "<div class=\"cq-msm-bpcell\">";
                html += data.text;
                html += "</div>";
                return html;
            },
            "listeners": {
                "contextmenu": function(e, elem, options) {
                    var data = {};
                    data["srcPath"] = options["node"]["attributes"]["path"];

                    this.showContextMenu(e.getXY(), [data]);
                    e.stopEvent();
                },
                "scope": this
            }
        }, this.initialConfig["treecolumn"]));

        var currentObj = this;

        var treeReload = function() {
            var v = currentObj.bpPathChooser.getValue();
            if (v && v != currentObj.rootPath) {
                currentObj.reconfigure(v);
            }
        };

        this.bpPathChooser = new CQ.form.PathField({
            "name": "blueprintSource",
            "width": 280,
            "value": this.rootPath,
            "listeners": {
                "render": function() {
                    CQ.Ext.QuickTips.register({
                        "target": this.getEl(),
                        "title":CQ.I18n.getMessage("Blueprint path"),
                        "text":CQ.I18n.getMessage("Choose the Blueprint root path of the tree"),
                        "autoHide":true
                    });
                },
                "specialkey": function(f, e) {
                    if (e.getKey() == e.ENTER) {
                        treeReload()
                    }
                },
                "change": function() {
                    treeReload();
                },
                "dialogSelect": function() {
                    treeReload();
                }
            }
        });

        this.tree = new CQ.wcm.msm.CloudwordsSource.Tree(CQ.Util.applyDefaults({
            "columns": columns,
            "url": this.url,
            "rootFilter": this.rootFilter,
            "rootPath": this.rootPath,
            "listeners": {
                "click": function(node) {
                    //alert(1);
                    // currentObj.grid.reload(node.attributes["path"]);
                }
            },
            "tbar": [
                this.bpPathChooser,{
                    "iconCls":"cq-siteadmin-refresh",
                    "handler":function() {
                        var v = this.bpPathChooser.getValue();
                        if (v && v != this.rootPath) {
                            this.reconfigure(v);
                        } else {
                            this.tree.reload();
                        }
                    },
                    "tooltip": {
                        "title":CQ.I18n.getMessage("Refresh tree"),
                        "text":CQ.I18n.getMessage("Refreshs the parent of the selected page"),
                        "autoHide":true
                    },
                    "scope": currentObj
                },{
                    "icon":"/etc/designs/cloudwords/icons/add.png",
                    "handler":function() {
                        //var v = this.bpPathChooser.getValue();
                        var path = "";
                        var title = "";
                        var node = this.tree.getSelectionModel().getSelectedNode();
                        if (node && node.attributes) {
                            path = node.attributes.path;
                            title = node.text;
                        }
                        if (path != "") {
                            //gridStore.add([v]);
                            this.grid.getStore().loadData([[path,title]],true);
                            this.grid.getView().refresh();
                            //alert(path);
                        } else {

                        }
                    },
                    "tooltip": {
                        "title":CQ.I18n.getMessage("Add to project"),
                        "text":CQ.I18n.getMessage("Adds the selected page to the project grid"),
                        "autoHide":true
                    },
                    "scope": this
                }
            ],
            "bbar": [{
                "text": "Back",
                "handler": function() {
                    CQ.Ext.Msg.show({
                        title: "Cancel Project?",
                        msg: "Are you sure you want to cancel this project and return to the welcome screen?",
                        buttons: CQ.Ext.Msg.YESNO,
                        fn: function(buttonId){
                            switch(buttonId){
                                case 'no':
                                    break;
                                case 'yes':
                                    window.location = "/libs/cq/core/content/welcome.html";
                                    break;
                            }
                        }
                    });
                },
                "scope": this
            }]
        }, this.initialConfig["tree"]));

        return this.tree;
    },

    getGrid: function(filter, maxCols) {

        var currentObj = this;
        this.grid = new CQ.Ext.grid.GridPanel(CQ.Util.applyDefaults({
            "id": "cq-cloudwords-grid",
            "height": 350,
            "cm":new CQ.Ext.grid.ColumnModel({
                "columns": [
                    {"id": "title", "header": "Title","width": 200, "sortable":true,dataIndex: "title"},
                    {"id": "path", "header": "Path","width": 300, "sortable":true,dataIndex: "path"}
                ]
            }),
            "viewConfig": new CQ.Ext.grid.GridView({}),
            "store": new CQ.Ext.data.ArrayStore({
                "storeId": "gridStore",
                "idIndex": 0,
                fields: [
                    'path',
                    'title'
                ]
            }),
            "bbar": [
                {
                    "icon":"/etc/designs/cloudwords/icons/delete.png",
                    "handler":function() {
                        var records = this.grid.getSelectionModel().getSelections();
                        for(var i = 0; i < records.length; i++) {
                            var record = records[i];
                            if(record) {
                                this.grid.getStore().remove(record);
                            }
                        }
                    },
                    "tooltip": {
                        "title":CQ.I18n.getMessage("Remove item"),
                        "text":CQ.I18n.getMessage("Removes the selected path from the grid"),
                        "autoHide":true
                    },
                    "scope": currentObj
                },{xtype: 'tbspacer', width: 680},{
                    "text": "Next",
                    "listeners": {
                        "click": {
                            "fn": function() {
                                var store = this.grid.getStore();
                                var url = "/etc/cloudwords/translationselector.savePaths.html?save"
                                store.each(function(r) {
                                    url = url + "&path=" + encodeURIComponent(r.json[0]);
                                },this);
                                //alert(url);
                                window.location = url;
                            },
                            "scope": this
                        }
                    }
                }
            ]
        }, this.initialConfig["grid"]));

        return this.grid;
    },

    checkGridReload: function() {

    },

    build: function() {
        //var myTree = this.getTree(myGrid.getStore());
        var myTree = this.getTree();
        var myGrid = this.getGrid(this.initialConfig.defaultLCFilter);
        this.treeContainer.add(myTree);
        //this.gridContainer.add(myGrid);
        this.doLayout();
    },

    redrawGrid: function(filter, maxCols) {
        filter = filter || this.grid.columnFilter;
        this.gridContainer.getEl().mask();
        this.gridContainer.remove(this.grid);
        this.headerStore = this.getHeaderStore(this.url, this.rootPath, filter);
        this.headerStore.load({
            "callback": function() {
                //this.gridContainer.add(this.getGrid(filter, maxCols));
                this.gridContainer.doLayout();
                this.gridContainer.getEl().unmask();
            },
            "scope": this});
    },

    reconfigure: function(rootPath) {
        this.rootPath = rootPath;
        this.tree.reconfigure(this.rootPath);
        //this.redrawGrid();
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        CQ.wcm.msm.CloudwordsSource.superclass.initComponent.call(this);
        this.on("render", function() {
            this.headerStore.load({
                "callback": this.build,
                "scope": this})
        }, this);
    }
});

CQ.Ext.reg("cloudwordssource", CQ.wcm.msm.CloudwordsSource);

/**
 * @class CQ.wcm.msm.CloudwordsSource.Tree
 * @extends CQ.Ext.Panel
 * The CloudwordsSource provides a tree panel to browse, view and select pages
 * to be translated.
 * @constructor
 * Creates a new CloudwordsSource.
 * @param {Object} config The config object
 */
CQ.wcm.msm.CloudwordsSource.Tree = CQ.Ext.extend(CQ.Ext.tree.TreePanel, {
    /**
     * @cfg {Object[]} columns
     * Columns of the grid.
     */
    columns: null,

    constructor: function(config) {
        config = (!config ? {} : config);

        var defaults = {
            "columns": config.columns,
            "loader": this.buildLoader(config.url, config.rootPath),
            "root": this.buildRootNode(config.rootPath)
        };

        CQ.Util.applyDefaults(config, defaults);

        // init component by calling super constructor
        CQ.wcm.msm.CloudwordsSource.Tree.superclass.constructor.call(this, config);
    },

    buildRootNode: function(rootPath) {
        return new CQ.Ext.tree.AsyncTreeNode({
            "expanded": true,
            "name": rootPath
        });
    },

    buildLoader: function(url, rootPath) {
        return new CQ.Ext.tree.TreeLoader({
            "dataUrl": url,
            "baseParams": {
                "isRoot": true,
                "isTree": true,
                "rootPath": rootPath
            },
            "requestMethod": "GET",
            "baseAttrs": {
                "singleClickExpand": true,
                "iconCls": "cq-msm-page",
                "uiProvider": CQ.wcm.msm.CloudwordsSource.ColumnNodeUI
            },
            "listeners": {
                "beforeload": function(loader, node) {
                    if (!this.baseParams.isRoot) {
                        this.baseParams.path = node.attributes["path"];
                    } else {
                        this.baseParams.path = this.baseParams.rootPath;
                    }
                },
                "load": function() {
                    //trick to manage the root differently
                    if (this.baseParams.isRoot) {
                        this.baseParams.isRoot = false;
                    }
                }
            }
        });
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        CQ.wcm.msm.CloudwordsSource.Tree.superclass.initComponent.call(this);
    },

    reconfigure: function(rootPath) {
        this.loader.baseParams.rootPath = rootPath;
        this.loader.baseParams.path = rootPath;
        this.loader.baseParams.isRoot = true;
        this.setRootNode(this.buildRootNode(rootPath));
    },

    setRootNode: function(node) {
        CQ.Ext.destroy(this.root);
        if(!node.render){ // attributes passed
            node = this.loader.createNode(node);
        }
        this.root = node;
        node.ownerTree = this;
        node.isRoot = true;
        if (!node.childNodes) {
            node.childNodes = [];
        }
        this.registerNode(node);
        if(!this.rootVisible){
            var uiP = node.attributes.uiProvider;
            node.ui = uiP ? new uiP(node) : new CQ.Ext.tree.RootTreeNodeUI(node);
        }
        if(this.innerCt){
            this.innerCt.update('');
            this.root.render();
            if(!this.rootVisible){
                this.root.renderChildren();
            }
        }
        return node;
    },

    reload: function() {
        var cnt = this.getEl().parent();
        cnt.mask("Loading...");
        try {
            var n = this.getSelectionModel().getSelectedNode();
            //if n, reload parent.
            if (n) {
                n = n.parentNode;
            }
            var root = this.getRootNode();
            if (!n || n == root) {
                //reloading root node does not work. then reload root first child.
                if (root.firstChild) {
                    n = root.firstChild;
                } else {
                    n = root;
                }
            }
            n.reload(function() {
                cnt.unmask();
            }, this);
        } catch (error) {
            cnt.unmask();
        }
    }
});

/**
 * Overrides column node UI.
 * @private
 */
CQ.wcm.msm.CloudwordsSource.ColumnNodeUI = CQ.Ext.extend(CQ.Ext.tree.TreeNodeUI, {
    focus: CQ.Ext.emptyFn, // prevent odd scrolling behavior

    renderElements : function(node, data, targetNode, bulkRender) {
        this.indentMarkup = node.parentNode ? node.parentNode.ui.getChildIndent() : '';

        var ownerTree = node.getOwnerTree();
        var cols = ownerTree.columns;
        var bw = ownerTree.borderWidth;
        var aCol = cols[0];
        var buf = [
            '<li class="x-tree-node"><div ext:tree-node-id="',node.id,'" class="x-tree-node-el x-tree-node-leaf ', data.cls,'">',
            '<div class="x-tree-col" style="width:',aCol.width - bw,'px;">',
            '<span class="x-tree-node-indent">',this.indentMarkup,"</span>",
            '<img src="', this.emptyIcon, '" class="x-tree-ec-icon x-tree-elbow">',
            '<img src="', data.icon || this.emptyIcon, '" class="x-tree-node-icon',(data.icon ? " x-tree-node-inline-icon" : ""),(data.iconCls ? " " + data.iconCls : ""),'" unselectable="on">',
            '<a hidefocus="on" class="x-tree-node-anchor" href="',data.href ? data.href : "#",'" tabIndex="1" ',
            data.hrefTarget ? ' target="' + data.hrefTarget + '"' : "", '>',
            '<span unselectable="on">', (aCol.renderer ? aCol.renderer(data[aCol.dataIndex], node, data) : node.text ? node.text : data[aCol.dataIndex]),"</span></a>",
            "</div>"];
        for (var i = 1, len = cols.length; i < len; i++) {
            aCol = cols[i];
            buf.push('<div class="x-tree-col ', (aCol.cls ? aCol.cls : ''), '" style="width:', aCol.width - bw, 'px;">',
                '<div class="x-tree-col-text">', (aCol.renderer ? aCol.renderer(data[aCol.dataIndex], node, data) : data[aCol.dataIndex]), "</div>",
                "</div>");
        }
        buf.push(
            '<div class="x-clear"></div></div>',
            '<ul class="x-tree-node-ct" style="display:none;"></ul>',
            "</li>");

        if (bulkRender !== true && node.nextSibling && node.nextSibling.ui.getEl()) {
            this.wrap = CQ.Ext.DomHelper.insertHtml("beforeBegin",
                node.nextSibling.ui.getEl(), buf.join(""));
        } else {
            this.wrap = CQ.Ext.DomHelper.insertHtml("beforeEnd", targetNode, buf.join(""));
        }

        this.elNode = this.wrap.childNodes[0];
        if (cols[0]["listeners"]) {
            var el = CQ.Ext.get(this.elNode);
            var scope = cols[0]["listeners"]["scope"] || el;
            for (var l in cols[0]["listeners"]) {
                if (l != "scope") {
                    el.on(l, cols[0]["listeners"][l], scope, {
                        "column": cols[0],
                        "data": data[cols[0].dataIndex],
                        "node": node,
                        "tree": ownerTree,
                        "location": "node"
                    });
                }
            }
        }

        this.ctNode = this.wrap.childNodes[1];
        var cs = this.elNode.firstChild.childNodes;
        this.indentNode = cs[0];
        this.ecNode = cs[1];
        this.iconNode = cs[2];
        this.anchor = cs[3];
        this.textNode = cs[3].firstChild;
        var colNode = this.elNode.firstChild.nextSibling;
        for (var i = 1, len = cols.length; colNode && i < len; i++) {
            var elem = CQ.Ext.get(colNode);
            if (cols[i]["listeners"]) {
                var scope = cols[i]["listeners"]["scope"] || elem;
                for (var l in cols[i]["listeners"]) {
                    if (l != "scope") {
                        elem.on(l, cols[i]["listeners"][l], scope, {
                            "column": cols[i],
                            "data": data[cols[i].dataIndex],
                            "node": node,
                            "tree": ownerTree,
                            "location": "node"
                        });
                    }
                }
            }
            colNode = colNode.nextSibling;
        }
    }
});

/**
 * @class CQ.wcm.msm.CloudwordsDeploy
 * @extends CQ.Ext.Panel
 * This provides a panel to select translation languages and the deploy paths for those languages
 * @constructor
 * Creates a new CloudwordsSource.
 * @param {Object} config The config object
 */
CQ.wcm.msm.CloudwordsDeploy = CQ.Ext.extend(CQ.Ext.Panel, {
    /**
     * @cfg {String} url
     * URL to retrieve the CloudwordsSource store.
     */
    url: null,

    /**
     * @cfg {String} rootPath
     * Path of the Blueprint
     */
    rootPath: null,

    contextMenus: [],

    constructor: function(config) {
        config = (!config ? {} : config);
        config = CQ.Util.applyDefaults(config, {
            //"url":"/libs/wcm/msm/content/commands/blueprintstatus.json",
            url:"/etc/cloudwords/util.liveCopies.js",
            rootPath:"/content",
            defaultLCFilter:""
        });

        this.url = config.url;
        this.rootPath = config.rootPath;
        this.lcMaxColumns = CQ.wcm.msm.CloudwordsSource.DEFAULT_MAX_VISIBLE_COLUMNS;
        this.headerStore = this.getHeaderStore(this.url, this.rootPath, config.defaultLCFilter);
        this.treeContainer = new CQ.Ext.Panel({
            "region": "west",
            "split": true,
            "layout":"fit",
            "collapsible": true,
            "collapseMode":"mini",
            "animate": true,
            "hideCollapseTool": true,
            "border": true,
            "width": 350
        });

        this.formContainer = new CQ.Ext.Panel({
            "region": "north",
            "height": 250,
            "layout": "fit",
            "border": true//,
            //"margins": "0 5 0 5"
        });

        var currentObj = this;

        this.sourceLanguageContainer = new CQ.Ext.Panel({
            layout: "fit",
            title: "Source Language Pages",
            height: 321,
            border: false
        });

        this.tabContainer = new CQ.Ext.TabPanel({
            region: "center",
            activeTab: 0,
            items: []

        });

        this.rightSideContainer = new CQ.Ext.Panel({
            region: "center",
            border: false,
            layout: "border",
            items: [

            ]
        });

        this.targetGrids = {codes: new Array()};
        this.currentLanguage = "";

        var defaults = {
            "layout": "border",
            "height": 600,
            "width": 1100,
            //default tree config
            "tree": {
                "lines": true,
                "border": false,
                "borderWidth": CQ.Ext.isBorderBox ? 0 : 2, // the combined left/right border for each cell
                "cls": "x-column-tree",
                "stateful":false,
                "rootVisible": false,
                "autoScroll":true
            },
            "form": {
                "border": false
            },
            "items": [

            ]
        };

        CQ.Util.applyDefaults(config, defaults);

        // init component by calling super constructor
        CQ.wcm.msm.CloudwordsDeploy.superclass.constructor.call(this, config);
    },

    getHeaderStore: function(url, rootPath, filter) {
        url = CQ.HTTP.addParameter(url, "headers", true);
        url = CQ.HTTP.addParameter(url, "rootPath", rootPath);
        url = CQ.HTTP.addParameter(url, "filter", filter || "");
        return new CQ.Ext.data.JsonStore({
            "proxy": new CQ.Ext.data.HttpProxy({
                "url": url
            }),
            "fields":["path", "name"]
        });
    },

    /**
     * Build the source language page list grid
     */
    getSourceLanguageGrid: function() {
        var currentObj = this;
        var sourcePathStore = new CQ.Ext.data.JsonStore({
            // store configs
            autoDestroy: true,
            autoLoad: true,
            storeId: "sourcePathStore",
            proxy: new CQ.Ext.data.HttpProxy({
                method: "GET",
                url: "/etc/cloudwords/util.sourcePaths.js"
            }),
            // reader configs
            root: "sourcePaths",
            idProperty: "path",
            fields: ["path", "title", {name: "includeChildren", type: "boolean"}]
        });

        this.sourceLangGrid = new CQ.Ext.grid.EditorGridPanel({
            id: "cq-cloudwords-sourcegrid",
            cm:new CQ.Ext.grid.ColumnModel({
                columns: [{
                    id: "title",
                    header: "Title",
                    width: 150,
                    sortable: true,
                    dataIndex: "title"
                },{
                    id: "path",
                    header: "Path",
                    width: 350,
                    sortable: true,
                    dataIndex: "path"
                },{
                    id: "includeChildren",
                    header: "Include Subpages",
                    width: 100,
                    dataIndex: "includeChildren",
                    xtype: "checkcolumn"
                }]
            }),
            "viewConfig": new CQ.Ext.grid.GridView({}),
            "store": sourcePathStore,
            border: false,
            listeners: {
                activate: {
                    fn: function(p) {
                        // javascript is really cool.
                        this.currentLanguage = "";
                    },
                    scope: currentObj
                }
            },
            bbar: [{ xtype: 'tbspacer', width: 650 },{
                xtype: "button",
                text: "Create Project",
                handler: function() {
                    // we want to save the data of the form
                    // first we save the source language stuff
                    // (whether or not subpages are translated)
                    this.saveSourcePaths();
                    // saveSourcePaths() will then call saveLanguagePaths()
                    // to save the target language path info
                },
                scope: currentObj
            }]
        });

        return this.sourceLangGrid;
    },

    /**
     * Builds internal tabs for each language
     */
    getTab: function(name,code) {
        var currentObj = this;
        var langStore = new CQ.Ext.data.ArrayStore({
            "storeId": code+"Store",
            "idIndex": 0,
            fields: [
                'path',
                'title'
            ]
        });
        var data = new Array();
        for(var i = 0; i < this.projectBase.targets.length; i++) {
            var target = this.projectBase.targets[i];
            if(target[2] == code) {
                // add this path
                data.push([target[0],target[1]]);
            }
        }
        if(data.length > 0) {
            langStore.loadData(data);
        }
        var langGrid = new CQ.wcm.msm.CloudwordsDeploy.LanguagePanel({
            "id": "cq-cloudwords-"+code+"grid",
            title: name,
            closable: true,
            //"height": "320",
            "cm":new CQ.Ext.grid.ColumnModel({
                "columns": [
                    {"id": "title", "header": "Title","width": 200, "sortable":true,dataIndex:"title"},
                    {"id": "path", "header": "Path","width": 300, "sortable":true,dataIndex:"path"}
                ]
            }),
            "viewConfig": new CQ.Ext.grid.GridView({}),
            "store": langStore,
            listeners: {
                activate: {
                    fn: function(p) {
                        // javascript is really cool.
                        this.currentLanguage = p.languageCode;
                    },
                    scope: currentObj
                },
                removed: {
                    fn: function(p) {
                        // remove grid from targetGrids list and language code from codes list
                        this.targetGrids[p.languageCode] = null;
                        this.targetGrids.codes.remove(p.languageCode);
                    },
                    scope: currentObj
                }
            },
            "bbar": [
                {
                    "icon":"/etc/designs/cloudwords/icons/delete.png",
                    "handler":function() {
                        var curLang = this.currentLanguage;
                        if(curLang) {
                            var store = this.targetGrids[curLang].getStore();
                            var selections = this.targetGrids[curLang].getSelectionModel().getSelections();
                            if(selections) {
                                for(var i = 0; i < selections.length; i++) {
                                    store.remove(selections[i]);
                                }
                            }
                        }
                        else {
                            alert("Unable to determine currently selected language.");
                        }
                    },
                    "tooltip": {
                        "title":CQ.I18n.getMessage("Remove item"),
                        "text":CQ.I18n.getMessage("Removes the selected path from the grid"),
                        "autoHide":true
                    },
                    "scope": currentObj
                },{ xtype: 'tbspacer', width: 630 },{
                    xtype: "button",
                    text: "Create Project",
                    handler: function() {
                        // we want to save the data of the form
                        // first we save the source language stuff
                        // (whether or not subpages are translated)
                        this.saveSourcePaths();
                        // saveSourcePaths() will then call saveLanguagePaths()
                        // to save the target language path info
                    },
                    scope: currentObj
                }
            ]
        });

        langGrid.languageCode = code;

        this.targetGrids[code] = langGrid;
        this.targetGrids.codes.push(code);

        return langGrid;
    },

    /**
     * Builds the browsing tree
     * @private
     */
    getTree: function(formStore) {
        var columns = new Array();
        columns.push(CQ.Util.applyDefaults({
            "dataIndex": "page",
            "width": 300,
            "renderer": function(nodeData, node, data) {
                var html = "<div class=\"cq-msm-bpcell\">";
                html += data.text;
                html += "</div>";
                return html;
            },
            "listeners": {
                "contextmenu": function(e, elem, options) {
                    var data = {};
                    data["srcPath"] = options["node"]["attributes"]["path"];

                    this.showContextMenu(e.getXY(), [data]);
                    e.stopEvent();
                },
                "scope": this
            }
        }, this.initialConfig["treecolumn"]));

        var currentObj = this;

        var treeReload = function() {
            var v = currentObj.bpPathChooser.getValue();
            if (v && v != currentObj.rootPath) {
                currentObj.reconfigure(v);
            }
        };

        this.bpPathChooser = new CQ.form.PathField({
            "name": "blueprintSource",
            "width": 280,
            "value": this.rootPath,
            "listeners": {
                "render": function() {
                    CQ.Ext.QuickTips.register({
                        "target": this.getEl(),
                        "title":CQ.I18n.getMessage("Blueprint path"),
                        "text":CQ.I18n.getMessage("Choose the Blueprint root path of the tree"),
                        "autoHide":true
                    });
                },
                "specialkey": function(f, e) {
                    if (e.getKey() == e.ENTER) {
                        treeReload();
                    }
                },
                "change": function() {
                    treeReload();
                },
                "dialogSelect": function() {
                    treeReload();
                }
            }
        });

        this.tree = new CQ.wcm.msm.CloudwordsSource.Tree(CQ.Util.applyDefaults({
            "columns": columns,
            "url": this.url,
            "rootFilter": this.rootFilter,
            "rootPath": this.rootPath,
            "listeners": {
                "click": function(node) {
                    //alert(1);
                    // currentObj.form.reload(node.attributes["path"]);
                }
            },
            "tbar": [
                this.bpPathChooser,{
                    "iconCls":"cq-siteadmin-refresh",
                    "handler":function() {
                        var v = this.bpPathChooser.getValue();
                        if (v && v != this.rootPath) {
                            this.reconfigure(v);
                        } else {
                            this.tree.reload();
                        }
                    },
                    "tooltip": {
                        "title":CQ.I18n.getMessage("Refresh tree"),
                        "text":CQ.I18n.getMessage("Refreshs the parent of the selected page"),
                        "autoHide":true
                    },
                    "scope": currentObj
                },{
                    "icon":"/etc/designs/cloudwords/icons/add.png",
                    "handler":function() {
                        var path = "";
                        var title = "";
                        var node = this.tree.getSelectionModel().getSelectedNode();
                        if (node && node.attributes) {
                            //if(node.attributes.validTarget) {
                            path = node.attributes.path;
                            title = node.text;
                            //} else {
                            //	if(this.currentLanguage != "") {
                            //		alert("That is not a valid translation target.");
                            //	}
                            //}
                        }
                        if (path != "" && this.currentLanguage != "") {
                            // find the right grid
                            this.targetGrids[this.currentLanguage].getStore().loadData([[path,title]],true);
                        } else {
                            // do nothing, no path to add
                        }
                    },
                    "tooltip": {
                        "title":CQ.I18n.getMessage("Add to project"),
                        "text":CQ.I18n.getMessage("Adds the selected page to the project form"),
                        "autoHide":true
                    },
                    "scope": currentObj
                }
            ],
            "bbar": [{
                "text": "Back",
                "handler": function() {
                    CQ.Ext.Msg.show({
                        title: "Cancel Project?",
                        msg: "Are you sure you want to cancel this project and return to the welcome screen?",
                        buttons: CQ.Ext.Msg.YESNO,
                        fn: function(buttonId){
                            switch(buttonId){
                                case 'no':
                                    break;
                                case 'yes':
                                    window.location = "/libs/cq/core/content/welcome.html";
                                    break;
                            }
                        }
                    });
                },
                "scope": this
            }]
        }, this.initialConfig["tree"]));

        return this.tree;
    },

    getForm: function(filter, maxCols) {

        var currentObj = this;

        var sourceLangStore = new CQ.Ext.data.JsonStore({
            // store configs
            //autoDestroy: true,
            autoLoad: true,
            storeId: "sourceLangStore",
            proxy: new CQ.Ext.data.HttpProxy({
                method: "GET",
                url: "/etc/cloudwords/util.sourceLangs.js"
            }),
            sortInfo: {
                field: 'name',
                direction: 'ASC'
            },
            // reader configs
            root: "languages",
            idProperty: "code",
            fields: ["name", "code"]
        });

        var targetLangStore = new CQ.Ext.data.JsonStore({
            // store configs
            //autoDestroy: true,
            autoLoad: true,
            storeId: "targetLangStore",
            proxy: new CQ.Ext.data.HttpProxy({
                method: "GET",
                url: "/etc/cloudwords/util.targetLangs.js"
            }),
            sortInfo: {
                field: 'name',
                direction: 'ASC'
            },
            // reader configs
            root: "languages",
            idProperty: "code",
            fields: ["name", "code"]
        });

        this.targetLanguageBox = new CQ.Ext.form.ComboBox({
            fieldLabel: "Target Languages",
            xtype: "combo",
            editable: false,
            store: targetLangStore,
            valueField: "code",
            displayField: "name",
            triggerAction: "all"
        });

        this.projectNameField = new CQ.Ext.form.TextField({
            fieldLabel: "Project Name"
        });

        this.basePathField = new CQ.Ext.form.TextField({
            fieldLabel: "Base Language Path"
        });

        this.sourceLanguageField = new CQ.Ext.form.ComboBox({
            fieldLabel: "Source Language",
            xtype: "combo",
            editable: false,
            store: sourceLangStore,
            valueField: "code",
            displayField: "name",
            triggerAction: "all"
        })

        this.form = new CQ.Ext.form.FormPanel(CQ.Util.applyDefaults({
            labelWidth : 150,
            title : "Cloudwords Project Setup",
            layout: "column",
            items : [{ // begin column 1
                xtype : "panel",
                columnWidth: 0.5,
                border: 0,
                items: [{ // begin project settings fieldset
                    xtype : "fieldset",
                    title : "Project Settings",
                    defaultType : "textfield",
                    margins: "5 0 0 0",
                    items : [

                    ]// end project settings fieldset
                },{// begin translation settings fieldset
                    xtype: "fieldset",
                    title: "Translation Settings",
                    items: [this.targetLanguageBox,{
                        fieldLabel: " ",
                        xtype: "button",
                        text: "Add Language",
                        handler: function() {
                            // we want to find the currently selected language
                            var code = this.targetLanguageBox.getValue();
                            if(code) {
                                var langRecord = this.targetLanguageBox.getStore().getById(code);
                                var name = langRecord.get("name");
                                var newTab = this.getTab(name,code);
                                this.tabContainer.add(newTab);
                                this.tabContainer.setActiveTab(newTab);
                            } else {
                                alert("You must choose a language");
                            }
                        },
                        "scope": currentObj
                    }]// end translation settings fieldset
                }]// end column 1
            },{// begin column 2
                xtype: "panel",
                columnWidth: 0.5,
                border: false,
                items: []
            }]// end column 2
        }, this.initialConfig["form"]));

        return this.form;
    },

    // save the subpage status of the source pages
    saveSourcePaths: function() {
        var store = this.sourceLangGrid.getStore();
        var url = "/etc/cloudwords/translationselector.saveSubpages.html?s";
        var foundSubpageCheck = false;
        store.each(function(r) {
            if(r.get("includeChildren")) {
                url+= "&path="+r.get("path");
                foundSubpageCheck = true;
            }
        });
        //alert(url);
        if(foundSubpageCheck) {
            // at least one was checked, we need to save
            CQ.Ext.Ajax.request({
                url: url,
                success: function(resp) {
                    // success, move on to language paths
                    this.saveLanguagePaths();
                },
                failure: function() {
                    failed = true;
                    alert("There was an error saving project data");
                },
                scope: this
            });
        } else {
            // none were checked, just move on to saving target languages
            this.saveLanguagePaths();
        }
    },

    saveLanguagePaths: function() {
        var codeList = new Array();
        var failed = false;
        for(var i = 0; i < this.targetGrids.codes.length; i++) {
            codeList[i]=this.targetGrids.codes[i];
        }
        for(var i = 0; i < this.targetGrids.codes.length; i++) {
            var code = this.targetGrids.codes[i];
            var grid = this.targetGrids[code];
            if(grid) {
                var store = grid.getStore();
                var url = "/etc/cloudwords/translationselector.saveLangs.html?langCode="+code;
                store.each(function(r) {
                    url += "&path="+r.get("path");
                });
                // now do the request
                CQ.Ext.Ajax.request({
                    url: url,
                    success: function(resp) {
                        codeList.remove(resp.responseText);
                        if(codeList.length == 0 && !failed) {
                            this.moveToConfirmPage();
                        }
                        else if(failed) {
                            //alert("There was an error saving project data");
                        }
                    },
                    failure: function() {
                        failed = true;
                        alert("There was an error saving project data");
                    },
                    scope: this
                });
                //alert(url);
            }
        }
    },

    moveToConfirmPage: function() {
        var confirmUrl = "/etc/cloudwords/translationselector.confirm.html";
        confirmUrl+= "?name="+this.projectNameField.getValue();
        confirmUrl+= "&basePath="+this.basePathField.getValue();
        confirmUrl+= "&sourceLang="+this.sourceLanguageField.getValue();
        window.location = confirmUrl;
    },

    checkGridReload: function() {

    },

    build: function() {
        //var myTree = this.getTree(myGrid.getStore());
        var sourceGrid = this.getSourceLanguageGrid();
        var myTree = this.getTree();
        var myForm = this.getForm(this.initialConfig.defaultLCFilter);
        this.sourceLanguageContainer.add(sourceGrid);
        this.treeContainer.add(myTree);
        this.formContainer.add(myForm);
        this.doLayout();
        CQ.Ext.Ajax.request({
            url: "/etc/cloudwords/util.projectBase.js",
            success: function(resp) {
                this.projectBase = CQ.Ext.decode(resp.responseText);
                this.basePathField.setValue(this.projectBase.sourceBasePath);
                // now we want to prepopulate the source language field
                this.sourceLanguageField.getStore().load({
                    callback: function() {
                        this.sourceLanguageField.setValue(this.projectBase.baseLanguage);
                    },
                    scope: this
                });
            },
            failure: function() {
                // if something failed, doesn't matter
            },
            scope: this
        });
        this.targetLanguageBox.getStore().load();
    },

    redrawForm: function(filter, maxCols) {
        filter = filter || this.form.columnFilter;
        this.formContainer.getEl().mask();
        this.formContainer.remove(this.form);
        this.headerStore = this.getHeaderStore(this.url, this.rootPath, filter);
        this.headerStore.load({
            "callback": function() {
                this.formContainer.add(this.getForm(filter, maxCols));
                this.formContainer.doLayout();
                this.formContainer.getEl().unmask();
            },
            "scope": this});
    },

    reconfigure: function(rootPath) {
        this.rootPath = rootPath;
        this.tree.reconfigure(this.rootPath);
        //this.redrawForm();
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        CQ.wcm.msm.CloudwordsDeploy.superclass.initComponent.call(this);
        this.on("render", function() {
            this.headerStore.load({
                "callback": this.build,
                "scope": this})
        }, this);
    }
});

CQ.Ext.reg("cloudwordsdeploy", CQ.wcm.msm.CloudwordsDeploy);

// This code was modified from the Ext.ConfirmPanel example located at:
// http://boran-extjs.blogspot.com/2011/03/extjs-confirm-close-of-tab-in-tabpanel.html
CQ.wcm.msm.CloudwordsDeploy.LanguagePanel = CQ.Ext.extend(CQ.Ext.grid.GridPanel, {

    initComponent: function(){

        CQ.wcm.msm.CloudwordsDeploy.LanguagePanel.superclass.initComponent.apply(this, arguments);

        this.addListener({
            beforeclose:{
                fn: this.onClose,
                scope: this
            }
        });

    },

    onClose: function(p){
        CQ.Ext.MessageBox.show({
            title: 'Remove this language?',
            msg: 'Are you sure you want to remove this language from the project? (This cannot be undone)',
            buttons: CQ.Ext.MessageBox.YESNOCANCEL,
            fn: function(buttonId){
                switch(buttonId){
                    case 'no':
                        // do nothing
                        //this.ownerCt.remove(p);
                        break;
                    case 'yes':
                        //this.saveToFile();
                        this.ownerCt.remove(p);  // manually removes tab from tab panel
                        break;
                    case 'cancel':
                        // leave blank if no action required on cancel
                        break;
                }
            },
            scope: this
        });
        return false;  // returning false to beforeclose cancels the close event
    },

    saveToFile: function(){
        //your code to save changes here
    }

});



/**
 * @class CQ.wcm.msm.CloudwordsProject
 * @extends CQ.Ext.Panel
 * This provides a panel to see project pages and get project information
 * @constructor
 * Creates a new CloudwordsConfirm.
 * @param {Object} config The config object
 */
CQ.wcm.msm.CloudwordsConfirm = CQ.Ext.extend(CQ.Ext.Panel, {
    /**
     * @cfg {String} url
     * URL to retrieve the CloudwordsSource store.
     */
    url: null,

    /**
     * @cfg {String} rootPath
     * Path of the Blueprint
     */
    rootPath: null,

    contextMenus: [],

    constructor: function(config) {
        config = (!config ? {} : config);
        config = CQ.Util.applyDefaults(config, {
            //"url":"/libs/wcm/msm/content/commands/blueprintstatus.json",
            url:"/etc/cloudwords/util.sourcePaths.js",
            rootPath:"/content",
            defaultLCFilter:""
        });

        this.url = config.url;
        this.rootPath = config.rootPath;
        this.lcMaxColumns = CQ.wcm.msm.CloudwordsSource.DEFAULT_MAX_VISIBLE_COLUMNS;
        this.headerStore = this.getHeaderStore(this.url, this.rootPath, config.defaultLCFilter);
        this.sourceContainer = new CQ.Ext.Panel({
            // NOTE: region should be west if right side is there
            //"region": "west",
            region: "center",
            "split": true,
            "layout":"fit",
            "collapsible": true,
            "collapseMode":"mini",
            "animate": true,
            "hideCollapseTool": true,
            "border": false,
            "width": 557
        });

        this.projectContainer = new CQ.Ext.Panel({
            title: "Project Information",
            "region": "north",
            "height": 250,
            "layout": "fit",
            "border": false//,
            //"margins": "0 5 0 5"
        });

        var currentObj = this;

        this.currentPageContainer = new CQ.Ext.Panel({
            layout: "fit",
            title: "Selected Page",
            region: "center",
            height: 321,
            border: false
        });

        this.rightSideContainer = new CQ.Ext.Panel({
            region: "center",
            border: false
            // NOTE: Items removed for now, eventually I would
            // like to make this have project info also
//        	layout: "border",
//        	items: [
//
//        	]
        });

        var defaults = {
            "layout": "border",
            "height": 300,
            "width": 650,
            //default tree config
            "tree": {
                "lines": true,
                "border": false,
                "borderWidth": CQ.Ext.isBorderBox ? 0 : 2, // the combined left/right border for each cell
                "cls": "x-column-tree",
                "stateful":false,
                "rootVisible": false,
                "autoScroll":true
            },
            "form": {
                "border": false
            },
            "items": [

            ]
        };

        CQ.Util.applyDefaults(config, defaults);

        // init component by calling super constructor
        CQ.wcm.msm.CloudwordsDeploy.superclass.constructor.call(this, config);
    },

    getHeaderStore: function(url, rootPath, filter) {
        url = CQ.HTTP.addParameter(url, "headers", true);
        url = CQ.HTTP.addParameter(url, "rootPath", rootPath);
        url = CQ.HTTP.addParameter(url, "filter", filter || "");
        return new CQ.Ext.data.JsonStore({
            "proxy": new CQ.Ext.data.HttpProxy({
                "url": "/etc/cloudwords/util.blank.js"
            }),
            "fields":["path", "name"]
        });
    },

    getProjectPane: function() {
        this.projectInfoValues = {};
        this.projectInfoValues.name = new CQ.Ext.form.Label({
            text: 'Project Name'
        });
        this.projectInfoValues.sourceLang = new CQ.Ext.form.Label({
            text: 'Project Name'
        });
        this.projectInfoValues.basePath = new CQ.Ext.form.Label({
            text: 'Project Name'
        });
        this.projectInfoValues.targetLangs = new CQ.Ext.Panel({
            border: false,
            layout: 'fit'
        });

        this.projectPane = new CQ.Ext.Panel({
            region: "center",
            border: false
        });
        return this.projectPane;
    },

    getCurrentPagePane: function() {
        this.currentPagePane = new CQ.Ext.Panel({
            region: "center",
            border: false
        });
        return this.currentPagePane;
    },

    build: function() {
        var myGrid = this.getGrid();
        //var myProject = this.getProjectPane();
        var myPage = this.getCurrentPagePane();

        this.sourceContainer.add(myGrid);
        //this.projectContainer.add(myProject);
        this.currentPageContainer.add(myPage);
        //this.sourcePathStore.load();
        this.doLayout();
    },

    reconfigure: function(rootPath) {
        this.rootPath = rootPath;
        //this.tree.reload();
        this.tree.reconfigure(this.rootPath);
        //this.redrawForm();
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        CQ.wcm.msm.CloudwordsProject.superclass.initComponent.call(this);
        this.on("render", function() {
            this.headerStore.load({
                "callback": this.build,
                "scope": this})
        }, this);
    }
});


CQ.wcm.msm.CloudwordsSource.DEFAULT_MAX_VISIBLE_COLUMNS = 22;
CQ.wcm.msm.CloudwordsSource.DISPLAY_COLUMNS_NUMBER_FITLER = 9;
