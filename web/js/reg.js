/**
 * Created by Administrator on 2015/10/20.
 */
$(function(){
    $('.submit').click(function(){
        if(check()){
            var username=encodeURIComponent($('input[name=username]').val());
            var password=encodeURIComponent($('input[name=password]').val());
            var data={
                "username":username,
                "password":password
            };
            ajaxRequest('/register',data,function(response){
                location.href='login.html';
            });
        }
    });
});

/*表单校验*/
function check(){
    var usernameVal=$('input[name=username]').val();
    var passwordVal=$('input[name=password]').val();
    var confirmPassVal=$('input[name=confirmPass]').val();
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