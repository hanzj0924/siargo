/**
 *  _____________________
 * |  _________________  |
 * | | jCalculator     | |
 * | |_________________| |
 * |  ___ ___ ___   ___  |
 * | | 7 | 8 | 9 | | + | |
 * | |___|___|___| |___| |
 * | | 4 | 5 | 6 | | - | |
 * | |___|___|___| |___| |
 * | | 1 | 2 | 3 | | x | |
 * | |___|___|___| |___| |
 * | | C | 0 | = | | / | |
 * | |___|___|___| |___| |
 * |_____________________|
 * 
 * @author: Marius Balaj & Adrian Valentin Dan
 * @contact: balajmarius93@gmail.com
 */


var template =
    '<div class="jcalculator d-none">' +
    '<span>7</span>' +
    '<span>8</span>' +
    '<span>9</span>' +
    '<span class="op">+</span>' +
    '<span>4</span>' +
    '<span>5</span>' +
    '<span>6</span>' +
    '<span class="op">-</span>' +
    '<span>1</span>' +
    '<span>2</span>' +
    '<span>3</span>' +
    '<span class="op">x</span>' +
    '<span>C</span>' +
    '<span>0</span>' +
    '<span class="op dot">·</span>' +
    '<span class="op">&divide;</span>' +
    '<span class="close">close</span>' +
    '<span class="eq">=</span>' +
    '</div>';


;
(function($) {


    $.fn.calculator = function(options) {

        function Controller(el) {
            var self = this;

            /**
             * Merge user options with defaults
             */

            this.options = $.extend({}, Controller.defaults, options);


            el.wrap('<div class="jcalculator_wrap"></div>');
            el.after(template);

            this.display = el;
            this.wrap = this.display.closest(".jcalculator_wrap");
            this.element = this.wrap.find(".jcalculator");
            this.inJBoltTable = isOk(this.element.closest("table[data-editable]"));
            if(this.inJBoltTable){
            	var pos = this.display.offset();
            	var top = pos.top+this.display.outerHeight()+1;
            	this.element.css({
            		top:top,
            		left:pos.left
            	});
                this.element.removeClass("d-none");
            }

            this.element.attr("id",randomId())

            /**
             * Add theme
             */

            this.element.addClass(this.options.theme);


            this.value = this.load();

            this.stack = null;
            this.stackOp = null;
            this.clearStack = true;

            var that=this;
            this.display.on("clear",function(){
                self.digit = "";
            })
            $('span', this.element).on('click', function(e) {
                e.preventDefault();
                e.stopPropagation()
                var code = $(this).text().trim();
                var va=$.trim(that.display.val());
                if(!self.v && va.length>0 && !isNaN(va) && va.charAt(0)!='-'){
                    for(var i=0;i<va.length;i++){
                        self.digit = va.charAt(i);
                    }
                }
                if (isNaN(code)) {
                    if(code == 'close'){
                        $(this).closest(".jcalculator").addClass("d-none");
                        if(that.inJBoltTable){
                            if(window.finishEditingCells){
                                var table = JBoltTableUtil.get($(this));
                                if(isOk(table)){
                                    finishEditingCells(table,true);
                                }
                            }
                        }
                        return false;
                    }
                	else if (code == 'C') {
                        self.digit;
                	} else if (code=='·') {
                		 self.digit = code;
                    } else if (code.charCodeAt(0) == 247) {
                        self.op = '/';
                    } else if (code.charCodeAt(0) == 61) {

                        self.op = code;

                        /**
                         * Results callback
                         */

                        self.options.onResult(self.value);

                    } else {
                        self.op = code;
                    }

                } else {

                        self.digit = code;

                }
                /**
                 * Material ripple effect
                 */
                         
                var $ripple;   

                if (self.options.theme == 'material') {

                    $ripple = $(this).find('.ripple');

                    if (!$ripple.length) {                        
                        $('<i class="ripple"></i>').appendTo($(this)).addClass('animate');
                    } else {
                        $ripple.removeClass('animate');
                        setTimeout(function() { $ripple.addClass('animate') }, 0);
                    }

                }

                /**
                 * onInput() callback
                 */

                self.options.onInput($(this), code);
                return false;
            });
        }
        
        Controller.prototype = {


            load: function() {
                return this.display.val() || this.display.text();
            },


            save: function() {
                if (this.display.is('input')) this.display.val(this.value).change();
                else this.display.text(this.value);
            },

            /**
             * Get value
             */

            get v() {
                return this.value;
            },

            /**
             * Set value
             */

            set v(val) {
                this.clearStack = false;
                this.value = val;
                this.save();
            },

            /**
             * Get operation
             */

            get op() {
                return this.stackOp;
            },


            /**
             * Set operation
             */

            set op(value) {

                switch (this.stackOp) {
                    case '+':
                        this.v = tofixed(this.stack + this.v,4,true);
                        break;
                    case '-':
                        this.v = tofixed(this.stack - this.v,4,true);
                        break;
                    case 'x':
                        this.v = tofixed(this.stack * this.v,4,true);
                        break;
                    case '/':
                    	this.v = tofixed(this.stack / this.v,4,true);
                    	break;
                }
                this.stack = this.v;
                this.stackOp = value;
                this.clearStack = true;
            },

            /**
             * Set digit
             */

            set digit(d) {
                if(d.length==0){
                    this.clearStack = true;
                    this.value = "";
                    this.stack = "";
                    this.save();
                    return "";
                }
            	if(this.v.toString().indexOf(".")!=-1){
            		if(d=='·'){
                		d = '.';
                		return this.v = this.v;
                	}
            		if (this.clearStack) return this.v = d;
            		return this.v = this.v+d;
                }
            	if(d=='·'){
            		d = '.';
            		return this.v = this.v+d
            	}
            	d = parseInt(d);
                if (this.clearStack) return this.v = d;
                return this.v = (this.v * 10) + d;
            },

            /**
             * Get digit
             */

            get digit() {
                if (this.clearStack) return this.v = 0;
                return this.v = Math.floor(this.v / 10);
            }

        };

        /**
         * Controllers defaults
         */

        Controller.defaults = {
            theme: 'material',
            onInput: function() {},
            onResult: function() {}
        };

        /**
         * Init jCalculator
         */

        var controller;

        this.each(function() {
            controller = new Controller($(this));
        });


        jboltBody.on('click', function(e) {
            var tar=$(e.target);
            if(isOk(tar.closest(".jcalculator_wrap")) || isOk(tar.closest(".jcalculator"))){
                var elel = tar.parent().find(".jcalculator");
                var idv = elel.attr("id");
                jboltBody.find(".jcalculator:not([id='"+idv+"'])").addClass("d-none");
                elel.removeClass("d-none");
            }else{
                controller.element.addClass("d-none");
            }
        });

        return controller;
    };

})(jQuery);
