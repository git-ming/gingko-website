/**
 * Created by Administrator on 2015/10/17.
 */
$(function(){

    //���Ա�ǩҳ
    $('.strategy-tab a').click(function (e) {
        e.preventDefault();
        $(this).tab('show');
    });

    /*//ͼƬ��Ϣ
    $('.tab-content img')
        .mouseover(function(e){
            event.stopPropagation();
            $(this).parent().next().show();
    })
        .mouseleave(function(e){
            event.stopPropagation();
            $(this).parent().next().hide();
    });*/
});
