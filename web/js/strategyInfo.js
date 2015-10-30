/**
 * Created by Administrator on 2015/10/26.
 */
$(function(){
    //获取文章信息
    getDocument();
    window.replyArr=[];
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

    //评论的分页
    $('.pagination').on('click','a',function(){
        var pageNo=$(this).attr('page');
        var pageNum=$('.pagination a:last').attr('page');
        var replyData=JSON.parse(sessionStorage.replyData);//数组和对象需要同json转义才可以存储在sessionStorage
        page(pageNum,pageNo,replyData);
    });

    //回复别人的评论
    $('.commentsList').on('click','.reply a',function(){
        var aim=$(this).parent().parent().find('.observer').html();
        $('.adaptArea textarea').val('回复'+aim+':');
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
        var pageNum=Math.ceil(response.reply.length/10);   //评论页数
        var pageNo=1;
        var replyData=response.reply;
        sessionStorage.replyData=JSON.stringify(replyData);
        var title=decodeURIComponent(response.title)||'无标题';
        var body=decodeURIComponent(response.body);
        var author=decodeURIComponent(response.author);
        $('.title').html(title);
        $('.author').html(author);
        $('.strategyInfo-body').html(body);
        $('.date').html(date);
        $('.comment').html(commentLen);
        $('.watched').html(response.reader);

        page(pageNum,pageNo,replyData);
    });
}

//获得对应页数的评论
function getCommentsByPage(data){
    $('.commentsList').empty();
    for(var i=0;i<data.length;i++){
        $('.commentsList').append($('#commentModal').html());
        var commentNo=$('.commentsList li').eq(i);
        var replyNo=data[i];
        var commentDate=transformDate(replyNo.data.$date);
        var observer=decodeURIComponent(replyNo.author);
        var commentContent=decodeURIComponent(replyNo.reply);
        commentNo.find('.observer').html(observer);
        commentNo.find('.comment-time').html(commentDate);
        commentNo.find('p').html(commentContent);
    }
}

//分页
function page(pageNum,pageNo,replyData){
    pageNum=parseInt(pageNum);
    pageNo=parseInt(pageNo);
    if(pageNum==1){
        $('.pagination').empty();
    }else{
        $('.pagination').empty()
            .append("<li><a href='#' page='1'>首页</a></li>" +
            "<li class='active'><a href='#'  page='"+pageNo+"'>"+pageNo+"</a></li>" +
            "<li><a href='#' page='"+pageNum+"'>尾页</a></li>");
        if(pageNo!=1)
            $('.pagination li:first').after("<li><a href='#' class='prev' page='"+(pageNo-1)+"'>上一页</a></li>");
        if(pageNo!=pageNum)
            $('.pagination li:last').before("<li><a href='#' class='next' page='"+(pageNo+1)+"'>下一页</a></li>");
        if(pageNo>4){
            $('.pagination .active').before("<li><a href='#'>...</a></li>")
                .before("<li><a href='#' page='"+(pageNo-3)+"'>"+(pageNo-3)+"</a></li>")
                .before("<li><a href='#' page='"+(pageNo-2)+"'>"+(pageNo-2)+"</a></li>")
                .before("<li><a href='#' page='"+(pageNo-1)+"'>"+(pageNo-1)+"</a></li>");
        }else{
            for(var i=1;i<pageNo;i++){
                $('.pagination .active').before("<li><a href='#' page='"+i+"'>"+i+"</a></li>");
            }
        }
        if(pageNum-pageNo>3){
            $('.pagination .active').after("<li><a href='#'>...</a></li>")
                .after("<li><a href='#' page='"+(pageNum)+"'>"+(pageNum)+"</a></li>")
                .after("<li><a href='#' page='"+(pageNum-1)+"'>"+(pageNum-1)+"</a></li>")
                .after("<li><a href='#' page='"+(pageNum-2)+"'>"+(pageNum-2)+"</a></li>");
        }else{
            for(i=pageNum;i>pageNo;i--){
                $('.pagination .active').after("<li><a href='#' page='"+i+"'>"+i+"</a></li>");
            }
        }
    }
    var data=replyData.slice((pageNo-1)*10,pageNo*10);
    getCommentsByPage(data);
}

//发送信息
function sendMessage(aim,message,preview){
    var data={
        aim:aim,
        message:message,
        preview:preview.length
    };
    ajaxHeader('/sendMessage',data,function(response){
        alert('发送成功');
    });
}
