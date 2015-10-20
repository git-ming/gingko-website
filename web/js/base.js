/**
 * Created by Administrator on 2015/10/20.
 */
window.username=window.sessionStorage.getItem('username');
window.password=window.sessionStorage.getItem('password');

$(function(){
//    判断当前用户已登录

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

