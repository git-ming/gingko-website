/**
 * Created by Administrator on 2015/10/20.
 */
$(function(){
    $('.submit').click(function(){
        var formObj={};
        formObj.usernameVal=$('input[name=username]').val();
        formObj.passwordVal=$('input[name=password]').val();
        formObj.confirmPassVal=$('input[name=confirmPass]').val();
        if(check(formObj)){
            /*var username=encodeURIComponent(formObj.usernameVal);
            var password=encodeURIComponent(formObj.passwordVal);
            register(username,password);*/
            checkUsername(formObj);
        }
    });
});

/*表单校验*/
function check(formObj){
    var usernameVal=formObj.usernameVal;
    var passwordVal=formObj.passwordVal;
    var confirmPassVal=formObj.confirmPassVal;
    if(!usernameVal||!passwordVal||!confirmPassVal){
        alert('不得为空');
        return false;
    }
    if(!/^[\w\u4e00-\u9fa5]*$/.test(usernameVal)){
        alert('用户名格式不正确');
        return false;
    }
    if(!/^[\w]*$/.test(passwordVal)){
        alert('密码格式不正确');
        return false;
    }
    if(!/^[\w]*$/.test(confirmPassVal)){
        return false;
    }
    if(confirmPassVal!=passwordVal){
        alert('密码不一致');
        return false;
    }
    return true;
}

//检测用户名是否可用
function checkUsername(formObj){
    var username=encodeURIComponent(formObj.usernameVal);
    var password=encodeURIComponent(formObj.passwordVal);
    var data={
        username:username
    };
    ajaxRequest('/checkUsername',data,function(){
        register(username,password);
    });
}

function register(username,password){
    var data={
        "username":username,
        "password":password
    };
    ajaxRequest('/register',data,function(response){
        location.href='login.html';
    });
}