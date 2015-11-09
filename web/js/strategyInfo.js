/**
 * Created by Administrator on 2015/10/26.
 */
$(function(){

    window.pageId=getQueryString('id');
    //閱讀人數加一
    if(!sessionStorage.visit){
        addDocumentReader();
        sessionStorage.visit=true;
    }

    //获取文章信息
    getDocument(window.pageId);
    window.replyArr=[];

    //点赞
    $('.up').parent().click(function(){
        //agree();
        isAgreed();
    });


    //评论
    $('.adaptArea textarea').keyup(function(){
        var textVal=$('.adaptArea textarea').val();
        $('.limit').html('最多输入'+(300-textVal.length)+'字');
        $('.adaptArea span').html(textVal);
    });

    //提交评论
    $('.myComment .submit').click(function(){
        var target=$('.myComment textarea');
        var comment=encodeURIComponent(target.val());
        if(comment){
            var aim=target.attr('data-aim');
            if(aim){
                var preview=comment.substr(0,50);
                sendMessage(aim,comment,preview.length);
            }
            reply(comment);
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
        $('.adaptArea textarea').val('回复'+aim+':').attr('data-aim',aim);
    });
});

//获取文章信息
function getDocument(pageId){
    var data={
        id:pageId
    };
    ajaxRequest('/getDocument',data,function(response){
        var date=transformDate(response.time.$date);
        var replyData=[];
        var commentLen=0;
        var pageNum=0;   //评论页数
        var pageNo=1;
        if(response.reply){
            replyData=response.reply;
            commentLen=response.reply.length;
            pageNum=Math.ceil(commentLen/10);
        }
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
    if(pageNum<=1){
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

    });
}

//评论
function reply(comment){
    var data={
        id:window.pageId,
        reply:comment
    };
    ajaxHeader('/reply',data,function(response){
        location.reload();
    });
}

//阅读人数加一
function addDocumentReader(){
    var data={
        id:window.pageId
    };
    ajaxRequest('/addDocumentReader',data,function(response){

    });
}


//点赞
function agree(){
    var data={
        id:window.pageId
    };
    ajaxHeader('/zan',data,function(response){

    });
}

//点赞
function disagree(){
    var data={
        id:window.pageId
    };
    ajaxHeader('/noZan',data,function(response){

    });
}

//点赞
function isAgreed(){
    var data={
        id:window.pageId
    };
    ajaxHeader('/isZan',data,function(response){

    });
}
