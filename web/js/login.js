/**
 * Created by Administrator on 2015/10/20.
 */
$(function(){
    $('.submit').click(function(){
        if(check()){
            window.sessionStorage.setItem('username',encodeURIComponent($('input[name=username]').val()));
            window.sessionStorage.setItem('password',encodeURIComponent($('input[name=password]').val()));
            $.ajax({
                url:'/login',
                type:'POST',
                dataType:'json',
                beforeSend:function(XML){
                    XML.setRequestHeader('username',encodeURIComponent($('input[name=username]').val()));
                    XML.setRequestHeader('password',encodeURIComponent($('input[name=password]').val()));
                },
                success:function(response){
                    if(response.return==200){
                        location.href='home.html';
                    }else{
                        alert('�û��˺Ż������д���');
                    }
                },
                error:function(response){
                    console.log(response);
                }
            });
        }
    });


});
function check(){
    var usernameVal=$('input[name=username]').val();
    var passwordVal=$('input[name=password]').val();
    if(!usernameVal||!passwordVal){
        alert('����Ϊ��');
        return false;
    }
    if(!/^[\w\u4e00-\u9fa5]*$/.test(usernameVal)){
        alert('�û�����ʽ����ȷ');
        return false;
    }
    if(!/^[\w]*$/.test(passwordVal)){
        alert('�����ʽ����ȷ');
        return false;
    }
    return true;
}
