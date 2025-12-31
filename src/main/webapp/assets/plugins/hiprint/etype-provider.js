var JBoltElementTypeProvider = (function () {
    return function (options) {

        var addElementTypes = function (context) {
            context.addPrintElementTypes(
                "designerModule",
                [
                    new hiprint.PrintElementTypeGroup("常规", [
                        { tid: 'designerModule.text', text: '文本', data: '', type: 'text' },
                        { tid: 'designerModule.image', text: '图片', data: 'assets/img/jfinalxylogo.png', type: 'image' },
                        { tid: 'designerModule.longText', text: '长文', data: '', type: 'longText' },
                        { tid: 'designerModule.barcode',text:"条形码",data: '123456789', type: 'text',
                        	options:{
                        		height: 50,
                        		width: 150,
                        		title: "123456789",
                        		textAlign: "center",
                        		textType: "barcode"
                        			
                        	} },
                        { tid: 'designerModule.qrcode',text:"二维码",data: 'jbolt极速开发平台', type: 'text',
                        	options:{
                              height: 100,
                              width: 100,
                              title:'jbolt极速开发平台',
                              textAlign: "center",
                              textType: "qrcode"
                        	
                        } },
                        {
                            tid: 'designerModule.table',
                            title: '空表格',
                            type: 'tableCustom'
                        },
                        {
                            tid: 'designerModule.html', title: 'html',
                            formatter: function (data, options) {
                                return $('<div style="width:200px;height:50px;border:1px dashed red;"></div>');
                            },
                            type: 'html'
                        },
                        { tid: 'designerModule.customText', text: '自定义文本', customText: '自定义文本', custom: true, type: 'text' }
                    ]),
                    new hiprint.PrintElementTypeGroup("辅助", [
                        {
                            tid: 'designerModule.hline',
                            text: '横线',
                            type: 'hline'
                        },
                        {
                            tid: 'designerModule.vline',
                            text: '竖线',
                            type: 'vline'
                        },
                        {
                            tid: 'designerModule.rect',
                            text: '矩形',
                            type: 'rect'
                        },
                        {
                            tid: 'designerModule.oval',
                            text: '椭圆',
                            type: 'oval'
                        }
                    ])
                ]
            );
        };

        return {
            addElementTypes: addElementTypes
        };

    };
})();


var hiprintDesignerDefaultConfigPrintJson = {
	    panels: [{
	    	index:0,
	    	paperType:"A4",
	        printElements: []
	    }]
	}
