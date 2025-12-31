var jbolt_login_js_version = "1.0.4";
/**
 * 登录页面使用
 */
//layer msg模块封装
var LayerMsgBox={
		alert:function(msg,icon,handler){
			if(icon){
				layer.alert(msg,{icon:icon}, function(index){
						if(handler){
							handler();
						}
					  layer.close(index);
					});  
			}else{
				layer.alert(msg, function(index){
					if(handler){
						handler();
					}
				  layer.close(index);
				});  
			}
			
		},
		/**
		 * 弹出成功信息,并执行回调方法
		 * @param msg
		 * @param time
		 * @param handler
		 */
		success:function(msg,time,handler){
			if(!msg){msg="操作成功";}
			if(!time){time=1000;}
			var index=layer.msg(msg,{time:time,icon:1},function(){
				if(handler){
					handler();
				}
			});
			return index;
		},


		/**
		 * 弹出Error,并执行回调方法
		 * @param msg
		 * @param time
		 */
		error:function(msg,time,handler){
			if(!msg){msg="错误";}
			if(!time){time=1500;}
			var index=layer.msg(msg,{time:time,icon:2},function(){
				if(handler){
					handler();
				}
			});
			return index;
		},
		/**
		 * 弹出进度
		 * @param msg
		 * @param time
		 */
		loading:function(msg,time,handler){
			if(!msg){msg="执行中...";}
			var index=null;
			time=(time?time:10000);
			if(time){
				index=layer.msg(msg,{time:time,icon:16,shade:0.3},function(){
					if(handler){
						handler();
					}
				});
			}else{
				index=layer.msg(msg,{icon:16});
			}
			return index;
		},
		closeLoading:function(){
			setTimeout(function(){
				layer.closeAll('dialog'); //关闭加载层
			}, 500);
		},
		closeLoadingNow:function(){
				layer.closeAll('dialog'); //关闭加载层
		}

}

/**
 * ajax封装
 */
var Ajax={
	 post:function(url,data,success,error){
				$.ajax({
					url:url,
					type:"post",
					dataType:"json",
					data:data,
					success:function(data){
						if(data.state=="ok"){
							if(success){
								success(data);
							}
						}else{
								LayerMsgBox.alert(data.msg,2);
								if(error){
									error();
								}
						}
					},
					error:function(){
						LayerMsgBox.alert("网络通讯异常",2);
						if(error){
							error();
						}
						
					}
					
				});
			}
}

function changeCaptcha(){
	$("#captcha").val("");
	$("#captchaimg").attr("src","admin/captcha?t="+new Date().getTime());
}
function checkForm(form){
	var username=$.trim(form.find("input[name='username']").val());
	if(!username){
		LayerMsgBox.error("请输入用户名");
		return false;
	}
	var password=$.trim(form.find("input[name='password']").val());
	if(!password){
		LayerMsgBox.error("请输入密码");
		return false;
	}
	var captchaInput=form.find("#captcha");
	if(captchaInput&&captchaInput.length>0){
		var captcha=$.trim(captchaInput.val());
		if(!captcha){
			LayerMsgBox.error("请输入验证码");
			return false;
		}
	}
	
	
	return true;
}
/**
 * 随机生成字符串
 * @param len 指定生成字符串长度
 */
function getRandomString(len){
	var _charStr = 'abacdefghjklmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ0123456789',
		min = 0,
		max = _charStr.length-1,
		_str = '';                    //定义随机字符串 变量
	//判断是否指定长度，否则默认长度为15
	len = len || 15;
	//循环生成字符串
	for(var i = 0, index; i < len; i++){
		index = (function(randomIndexFunc, i){
			return randomIndexFunc(min, max, i, randomIndexFunc);
		})(function(min, max, i, _self){
			let indexTemp = Math.floor(Math.random()*(max-min+1)+min),
				numStart = _charStr.length - 10;
			if(i==0&&indexTemp >=numStart){
				indexTemp = _self(min, max, i, _self);
			}
			return indexTemp ;
		}, i);
		_str += _charStr[index];
	}
	return _str;
}

function md5(string){
	function md5_RotateLeft(lValue, iShiftBits) {
		return (lValue<<iShiftBits) | (lValue>>>(32-iShiftBits));
	}
	function md5_AddUnsigned(lX,lY){
		var lX4,lY4,lX8,lY8,lResult;
		lX8 = (lX & 0x80000000);
		lY8 = (lY & 0x80000000);
		lX4 = (lX & 0x40000000);
		lY4 = (lY & 0x40000000);
		lResult = (lX & 0x3FFFFFFF)+(lY & 0x3FFFFFFF);
		if (lX4 & lY4) {
			return (lResult ^ 0x80000000 ^ lX8 ^ lY8);
		}
		if (lX4 | lY4) {
			if (lResult & 0x40000000) {
				return (lResult ^ 0xC0000000 ^ lX8 ^ lY8);
			} else {
				return (lResult ^ 0x40000000 ^ lX8 ^ lY8);
			}
		} else {
			return (lResult ^ lX8 ^ lY8);
		}
	}
	function md5_F(x,y,z){
		return (x & y) | ((~x) & z);
	}
	function md5_G(x,y,z){
		return (x & z) | (y & (~z));
	}
	function md5_H(x,y,z){
		return (x ^ y ^ z);
	}
	function md5_I(x,y,z){
		return (y ^ (x | (~z)));
	}
	function md5_FF(a,b,c,d,x,s,ac){
		a = md5_AddUnsigned(a, md5_AddUnsigned(md5_AddUnsigned(md5_F(b, c, d), x), ac));
		return md5_AddUnsigned(md5_RotateLeft(a, s), b);
	};
	function md5_GG(a,b,c,d,x,s,ac){
		a = md5_AddUnsigned(a, md5_AddUnsigned(md5_AddUnsigned(md5_G(b, c, d), x), ac));
		return md5_AddUnsigned(md5_RotateLeft(a, s), b);
	};
	function md5_HH(a,b,c,d,x,s,ac){
		a = md5_AddUnsigned(a, md5_AddUnsigned(md5_AddUnsigned(md5_H(b, c, d), x), ac));
		return md5_AddUnsigned(md5_RotateLeft(a, s), b);
	};
	function md5_II(a,b,c,d,x,s,ac){
		a = md5_AddUnsigned(a, md5_AddUnsigned(md5_AddUnsigned(md5_I(b, c, d), x), ac));
		return md5_AddUnsigned(md5_RotateLeft(a, s), b);
	};
	function md5_ConvertToWordArray(string) {
		var lWordCount;
		var lMessageLength = string.length;
		var lNumberOfWords_temp1=lMessageLength + 8;
		var lNumberOfWords_temp2=(lNumberOfWords_temp1-(lNumberOfWords_temp1 % 64))/64;
		var lNumberOfWords = (lNumberOfWords_temp2+1)*16;
		var lWordArray=Array(lNumberOfWords-1);
		var lBytePosition = 0;
		var lByteCount = 0;
		while ( lByteCount < lMessageLength ) {
			lWordCount = (lByteCount-(lByteCount % 4))/4;
			lBytePosition = (lByteCount % 4)*8;
			lWordArray[lWordCount] = (lWordArray[lWordCount] | (string.charCodeAt(lByteCount)<<lBytePosition));
			lByteCount++;
		}
		lWordCount = (lByteCount-(lByteCount % 4))/4;
		lBytePosition = (lByteCount % 4)*8;
		lWordArray[lWordCount] = lWordArray[lWordCount] | (0x80<<lBytePosition);
		lWordArray[lNumberOfWords-2] = lMessageLength<<3;
		lWordArray[lNumberOfWords-1] = lMessageLength>>>29;
		return lWordArray;
	};
	function md5_WordToHex(lValue){
		var WordToHexValue="",WordToHexValue_temp="",lByte,lCount;
		for(lCount = 0;lCount<=3;lCount++){
			lByte = (lValue>>>(lCount*8)) & 255;
			WordToHexValue_temp = "0" + lByte.toString(16);
			WordToHexValue = WordToHexValue + WordToHexValue_temp.substr(WordToHexValue_temp.length-2,2);
		}
		return WordToHexValue;
	};
	function md5_Utf8Encode(string){
		string = string.replace(/\r\n/g,"\n");
		var utftext = "";
		for (var n = 0; n < string.length; n++) {
			var c = string.charCodeAt(n);
			if (c < 128) {
				utftext += String.fromCharCode(c);
			}else if((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			} else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}
		}
		return utftext;
	};
	var x=Array();
	var k,AA,BB,CC,DD,a,b,c,d;
	var S11=7, S12=12, S13=17, S14=22;
	var S21=5, S22=9 , S23=14, S24=20;
	var S31=4, S32=11, S33=16, S34=23;
	var S41=6, S42=10, S43=15, S44=21;
	string = md5_Utf8Encode(string);
	x = md5_ConvertToWordArray(string);
	a = 0x67452301; b = 0xEFCDAB89; c = 0x98BADCFE; d = 0x10325476;
	for (k=0;k<x.length;k+=16) {
		AA=a; BB=b; CC=c; DD=d;
		a=md5_FF(a,b,c,d,x[k+0], S11,0xD76AA478);
		d=md5_FF(d,a,b,c,x[k+1], S12,0xE8C7B756);
		c=md5_FF(c,d,a,b,x[k+2], S13,0x242070DB);
		b=md5_FF(b,c,d,a,x[k+3], S14,0xC1BDCEEE);
		a=md5_FF(a,b,c,d,x[k+4], S11,0xF57C0FAF);
		d=md5_FF(d,a,b,c,x[k+5], S12,0x4787C62A);
		c=md5_FF(c,d,a,b,x[k+6], S13,0xA8304613);
		b=md5_FF(b,c,d,a,x[k+7], S14,0xFD469501);
		a=md5_FF(a,b,c,d,x[k+8], S11,0x698098D8);
		d=md5_FF(d,a,b,c,x[k+9], S12,0x8B44F7AF);
		c=md5_FF(c,d,a,b,x[k+10],S13,0xFFFF5BB1);
		b=md5_FF(b,c,d,a,x[k+11],S14,0x895CD7BE);
		a=md5_FF(a,b,c,d,x[k+12],S11,0x6B901122);
		d=md5_FF(d,a,b,c,x[k+13],S12,0xFD987193);
		c=md5_FF(c,d,a,b,x[k+14],S13,0xA679438E);
		b=md5_FF(b,c,d,a,x[k+15],S14,0x49B40821);
		a=md5_GG(a,b,c,d,x[k+1], S21,0xF61E2562);
		d=md5_GG(d,a,b,c,x[k+6], S22,0xC040B340);
		c=md5_GG(c,d,a,b,x[k+11],S23,0x265E5A51);
		b=md5_GG(b,c,d,a,x[k+0], S24,0xE9B6C7AA);
		a=md5_GG(a,b,c,d,x[k+5], S21,0xD62F105D);
		d=md5_GG(d,a,b,c,x[k+10],S22,0x2441453);
		c=md5_GG(c,d,a,b,x[k+15],S23,0xD8A1E681);
		b=md5_GG(b,c,d,a,x[k+4], S24,0xE7D3FBC8);
		a=md5_GG(a,b,c,d,x[k+9], S21,0x21E1CDE6);
		d=md5_GG(d,a,b,c,x[k+14],S22,0xC33707D6);
		c=md5_GG(c,d,a,b,x[k+3], S23,0xF4D50D87);
		b=md5_GG(b,c,d,a,x[k+8], S24,0x455A14ED);
		a=md5_GG(a,b,c,d,x[k+13],S21,0xA9E3E905);
		d=md5_GG(d,a,b,c,x[k+2], S22,0xFCEFA3F8);
		c=md5_GG(c,d,a,b,x[k+7], S23,0x676F02D9);
		b=md5_GG(b,c,d,a,x[k+12],S24,0x8D2A4C8A);
		a=md5_HH(a,b,c,d,x[k+5], S31,0xFFFA3942);
		d=md5_HH(d,a,b,c,x[k+8], S32,0x8771F681);
		c=md5_HH(c,d,a,b,x[k+11],S33,0x6D9D6122);
		b=md5_HH(b,c,d,a,x[k+14],S34,0xFDE5380C);
		a=md5_HH(a,b,c,d,x[k+1], S31,0xA4BEEA44);
		d=md5_HH(d,a,b,c,x[k+4], S32,0x4BDECFA9);
		c=md5_HH(c,d,a,b,x[k+7], S33,0xF6BB4B60);
		b=md5_HH(b,c,d,a,x[k+10],S34,0xBEBFBC70);
		a=md5_HH(a,b,c,d,x[k+13],S31,0x289B7EC6);
		d=md5_HH(d,a,b,c,x[k+0], S32,0xEAA127FA);
		c=md5_HH(c,d,a,b,x[k+3], S33,0xD4EF3085);
		b=md5_HH(b,c,d,a,x[k+6], S34,0x4881D05);
		a=md5_HH(a,b,c,d,x[k+9], S31,0xD9D4D039);
		d=md5_HH(d,a,b,c,x[k+12],S32,0xE6DB99E5);
		c=md5_HH(c,d,a,b,x[k+15],S33,0x1FA27CF8);
		b=md5_HH(b,c,d,a,x[k+2], S34,0xC4AC5665);
		a=md5_II(a,b,c,d,x[k+0], S41,0xF4292244);
		d=md5_II(d,a,b,c,x[k+7], S42,0x432AFF97);
		c=md5_II(c,d,a,b,x[k+14],S43,0xAB9423A7);
		b=md5_II(b,c,d,a,x[k+5], S44,0xFC93A039);
		a=md5_II(a,b,c,d,x[k+12],S41,0x655B59C3);
		d=md5_II(d,a,b,c,x[k+3], S42,0x8F0CCC92);
		c=md5_II(c,d,a,b,x[k+10],S43,0xFFEFF47D);
		b=md5_II(b,c,d,a,x[k+1], S44,0x85845DD1);
		a=md5_II(a,b,c,d,x[k+8], S41,0x6FA87E4F);
		d=md5_II(d,a,b,c,x[k+15],S42,0xFE2CE6E0);
		c=md5_II(c,d,a,b,x[k+6], S43,0xA3014314);
		b=md5_II(b,c,d,a,x[k+13],S44,0x4E0811A1);
		a=md5_II(a,b,c,d,x[k+4], S41,0xF7537E82);
		d=md5_II(d,a,b,c,x[k+11],S42,0xBD3AF235);
		c=md5_II(c,d,a,b,x[k+2], S43,0x2AD7D2BB);
		b=md5_II(b,c,d,a,x[k+9], S44,0xEB86D391);
		a=md5_AddUnsigned(a,AA);
		b=md5_AddUnsigned(b,BB);
		c=md5_AddUnsigned(c,CC);
		d=md5_AddUnsigned(d,DD);
	}
	return (md5_WordToHex(a)+md5_WordToHex(b)+md5_WordToHex(c)+md5_WordToHex(d)).toLowerCase();
}
var _submit_ing=false;
//提交登录
function submitForm(form){
		if(_submit_ing){
			return false;
		}
		_submit_ing=true;
		var sf=$(form);
		if(checkForm(sf)){
			LayerMsgBox.loading("登录中...",30000);
			var pwdInput = sf.find("input[type='password'][name='password']");
			var passwordValue = pwdInput.val();
			var left = getRandomString(2);
			var right = getRandomString(3);
			pwdInput.val(left+md5(passwordValue)+right);
			Ajax.post("admin/login",sf.serialize(),function(res){
				LayerMsgBox.success("登录成功",300,function(){
					_submit_ing=false;
					var base=$("base").attr("href");
					if(base.charAt(base.length-1)=='/'){
						window.location.href=base+"admin";
					}else{
						window.location.href=base+"/admin";
					}
				});
			},function(){
				_submit_ing=false;
				pwdInput.val("");
				changeCaptcha();
			})
		}else{
			_submit_ing=false;
		}
		return false;
	}

	/**
	 * 初始化登录页面样式
	 * @returns
	 */
	function initLoginStyle(){
		//var glassStyle=$.cookie('jbolt_login_glassStyle');
		var bgimgBlur=$.cookie('jbolt_login_bgimgBlur');
		var needNest=$.cookie('jbolt_login_nest');
		
		// if(glassStyle&&glassStyle=="true"){
		// 	$(".jbolt_loginform").addClass("jbolt_bg_glass");
		// }else{
		// 	$(".jbolt_loginform").removeClass("jbolt_bg_glass");
		// }
		if(bgimgBlur&&bgimgBlur=="true"){
			$(".bodybgmask").addClass("blur");
		}else{
			$(".bodybgmask").removeClass("blur");
		}
		var startnest=true;
		if(needNest&&needNest=="false"||(!needNest&&systemLoginNest=="false")){
			startnest=false;
			//用户自己设置了false或者系统全局设置了false 但是用户的设置还未初始化的时候 就不启动nest
		}
		if(startnest){
			nest();
		}

	}
	/**
	 * 调用nest特效
	 * @returns
	 */
	function nest(){
		$.getScript("assets/plugins/canvas-nest/canvas-nest.umd.js").
		done(function(){
			new CanvasNest(document.body,{
			    color: '255,255,255',
			    opacity:0.5,
			    zIndex:1,
			    count: 200,
			  });
		});
		
	}

	$(function(){
		initLoginStyle();
		/*if(self!=top){
			if(parent.showReloginDialog){
				parent.showReloginDialog();
			}
		}else{
			initLoginStyle();
		}*/
		});