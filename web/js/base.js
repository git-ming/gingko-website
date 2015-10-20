/**
 * Created by Administrator on 2015/10/20.
 */
window.username=window.sessionStorage.getItem('username');
window.password=window.sessionStorage.getItem('password');

$(function(){
//    判断当前用户已登录
    console.log(window.username&&window.password);
    if(window.username&&window.password){
        console.log(1);
        $('.user-info')
            .html('<li><a href="#"><img src="../images/logo.png"><span class="badge">99</span></a>' +
                    '</li><li class="logout"><a href="login.html">退出</a></li>')
            .css('position','relative');
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
            XML.setRequestHeader('username',window.username);
            XML.setRequestHeader('password',window.password);
        },
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

