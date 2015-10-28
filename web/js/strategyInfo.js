/**
 * Created by Administrator on 2015/10/26.
 */
$(function(){
//    评论
    $('.submit').click(function(){

    });
    $('.adaptArea textarea').keyup(function(){
        var textVal=$('.adaptArea textarea').val();
        $('.limit').html('最多输入'+(300-textVal.length)+'字');
        $('.adaptArea span').html(textVal);
    });
});
