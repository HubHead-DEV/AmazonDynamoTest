Ext.application({
    name: 'CloudTest',

    launch: function() {
        Ext.Loader.setConfig({
            enabled:true,
            paths: {
                com:"src/com"
            }
        });

        this.showLogin();


    },

    showLogin: function() {
    
    	var form=new Ext.form.FormPanel({
            bodyPadding: 5,
            width: 350,
            url: '/login',
            action: 'POST',
            defaults: {width:230} ,
            title: 'CloudTest Log in',
            items: [
                {
                    xtype: 'textfield',
                    fieldLabel: 'Username',
                    name: 'username'
                },
                {
                    xtype: 'textfield',
                    fieldLabel: 'Password',
                    inputType: 'password',
                    name: 'password'
                }
            ],
            buttons: [{
                    text: 'Log in',
                    formBind: true,
                    handler: function() {
                        form.getForm().submit({
                            waitMsg:'Please wait...',
                            reset:true,
                            scope:this,
                            success: function(form,action) {
                                Ext.Msg.alert('Status', 'Login Successful!',
                                    function(btn, text){
                                        if (btn == 'ok'){
                                            window.location = 'list.html';
                                        }
                                    }
                                );
                            },
                            failure: function(form, action) {
                                if(action.failureType == 'server') {
                                    obj = Ext.util.JSON.decode(action.response.responseText);
                                    Ext.Msg.alert('Login Failed!', obj.msg);
                                } else {
                                    Ext.Msg.alert('Warning!', 'Authentication server is unreachable : ' + action.response.responseText);
                                }
                                form.getForm().reset();
                            }
                        });
                    }
            }
            ]
        });

        var viewport=new Ext.Viewport({
          layout: {
            type:'vbox',
            align:'center',
            pack:'center'
          },
          items:[form]
          });
    }
})