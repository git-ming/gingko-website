/**
 * Created by Administrator on 2015/10/20.
 */


$(function(){
//    判断当前用户已登录
    if(sessionStorage.username&&sessionStorage.password){
        $('.user-info')
            .html('<li><a href="user.html"><img src="../images/logo.png"><span class="badge">99</span></a>' +
            '</li><li class="logout"><a href="login.html">退出</a></li>')
            .css('position','relative');
        userInfo();
        getUnreadMessageSize();
    }else{
        $('.user-info').html('<li><a  href="login.html">登录</a></li><li><a href="reg.html">注册</a></li>');
    }

    //退出清除当前用户信息
    $('.logout').click(function(){
        sessionStorage.clear();
    });
});


//头部需要加username的ajax
function ajaxHeader(url,data,callback){
    $.ajax({
        url:url,
        type:'POST',
        dataType:'json',
        data:JSON.stringify(data),
        beforeSend:function(XML){
            XML.setRequestHeader('username',sessionStorage.username);
            XML.setRequestHeader('password',sessionStorage.password);
        },
        success:function(response){
            if(response.return==200){
                callback(response.data);
            }else if(response.return==404){
                alert('找不到页面');
            }else if(response.return==403){
                alert('操作有误');
            }
        },
        error:function(response){
            console.log(response);
        }
    });
}

//不需要加头部的ajax
function ajaxRequest(url,data,callback){
    $.ajax({
        url:url,
        type:'POST',
        dataType:'json',
        data:JSON.stringify(data),
        success:function(response){
            if(response.return==200){
                callback(response.data);
            }else if(response.return==404){
                alert('找不到页面');
            }else if(response.return=403){
                alert('操作有误');
            }
        },
        error:function(response){
            console.log(response);
        }
    });
}

//转换时间
function transformDate(time){
    var date=new Date(time)||new Date();
    var year=date.getFullYear();
    var month=date.getMonth()+1;
    var day=date.getDate();
    var hour=date.getHours();
    var minute=date.getMinutes();
    return year+'-'+month+'-'+day+'　'+hour+':'+minute;
}

//获取url中的数据
function getQueryString(name){
    var reg=new RegExp("(^|&)"+name+"=([^&]*)($|&)","i");
    var str=document.location.search.substr(1).match(reg);
    if(str) return str[2];
    return null;
}

//按时间排序
function rankByTime(data){
    var oldArr=[];
    var rankArr=[];
    var newData=[];
    //按时间排序
    for(var i=0;i<data.length;i++){
        oldArr.push(data[i].time.time);
        rankArr.push(data[i].time.time);
    }
    rankArr.sort();
    for(i=0;i<rankArr.length;i++){
        var rank=oldArr.indexOf(rankArr[i]);
        newData.push(data[rank]);
    }
    return newData;
}

//获取当前用户信息
function userInfo(){
    ajaxHeader('/userInfo',null,function(response){
        var headImgPath=response.headImg||'../images/logo.png';

    });
}

//获取用户未读消息数量
function getUnreadMessageSize(){
    ajaxHeader('/getUnreadMessageSize',null,function(response){
        var messageNum=parseInt(response.return)||0;
        if(messageNum<=0){
            $('.user-info span').empty();
        }else{
            $('.user-info .badge').html(messageNum);
        }
    });
}