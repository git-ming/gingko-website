/**
 * Created by Administrator on 2015/10/26.
 */
$(function(){
    //获取文章信息
    getDocument();

    //评论
    $('.adaptArea textarea').keyup(function(){
        var textVal=$('.adaptArea textarea').val();
        $('.limit').html('最多输入'+(300-textVal.length)+'字');
        $('.adaptArea span').html(textVal);
    });

    $('.myComment .submit').click(function(){
        var comment=encodeURIComponent($('.myComment textarea').val());
        if(comment){
            var data={
                id:getQueryString('id'),
                reply:comment
            };
            ajaxHeader('/reply',data,function(response){
                location.reload();
            });
        }else{
            alert('不得为空');
        }
    });
});

//获取文章信息
function getDocument(){
    var data={
        id:getQueryString('id')
    };
    ajaxRequest('/getDocument',data,function(response){
        var date=transformDate(response.time.$date);
        var commentLen=response.reply?response.reply.length:0;
        var title=decodeURIComponent(response.title)||'无标题';
        var body=decodeURIComponent(response.body);
        var author=decodeURIComponent(response.author);
        $('.title').html(title);
        $('.author').html(author);
        $('.strategyInfo-body').html(body);
        $('.date').html(date);
        $('.comment').html(commentLen);
        $('.watched').html(response.reader);

        $('.commentsList').empty();
        if(commentLen>0){
            for(var i=0;i<commentLen;i++){
                $('.commentsList').append($('#commentModal').html());
                var commentNo=$('.commentsList li').eq(i);
                var replyNo=response.reply[i];
                var commentDate=transformDate(replyNo.data.$date);
                var observer=decodeURIComponent(replyNo.author);
                var commentContent=decodeURIComponent(replyNo.reply);
                commentNo.find('.observer').html(observer);
                commentNo.find('.comment-time').html(commentDate);
                commentNo.find('p').html(commentContent);
            }
        }else{
            $('.commentsList').empty().html('暂无评论');
        }

    });
}
