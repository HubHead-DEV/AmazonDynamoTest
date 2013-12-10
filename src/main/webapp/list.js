Ext.onReady(function () {
    var itemsPerPage = 20; // set the number of items you want per page
    var store = Ext.create('Ext.data.Store', {
        id: 'simpsonsStore',
        autoLoad: false,
        fields: ['username', 'password'],
        pageSize: itemsPerPage, // items per page
        proxy: {
            type: 'ajax',
            url: '../login/', // url that will load data with respect to start and limit params
            reader: {
                type: 'json',
                root: 'logins',
                totalProperty: 'total'
            }
        }
    });

    // specify the page you want to load
    store.loadPage(1);

    Ext.create('Ext.grid.Panel', {
        title: 'Logins',
        store: store,
        columns: [{
            header: 'Username',
            dataIndex: 'username'
        }, {
            header: 'Password',
            dataIndex: 'password',
            flex: 1
        }],
        width: 800,
        height: 800,
        dockedItems: [{
            xtype: 'pagingtoolbar',
            store: store, // same store GridPanel is using
            dock: 'bottom',
            displayInfo: true
        }],
        renderTo: Ext.getBody()
    });
});
