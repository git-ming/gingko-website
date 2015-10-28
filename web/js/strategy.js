$(function(){

    //获取攻略列表
    var data={
        author:123,
        page:2
    };
    getDocumentList(data);
});
function getDocumentList(data){
    ajaxRequest('/getDocumentList',data,function(response){
        var strategyNum=response.length;
        if(strategyNum>0){
            for(var i=0;i<strategyNum;i++){
                $('.strategy-list').append($('#strategy-modal').html());
                var target=$('.strategy-list-item').eq(i);
                /*判空过滤*/
                var strategyId=response[i].id||0;
                var title=decodeURIComponent(response[i].title)||'无标题';
                var headImg=response[i].headImg||'../images/logo.png';
                var authorName=response[i].author||'admin';
                var date=new Date(response[i].time.time)||new Date();
                var time=date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'　'+date.getHours()+':'+date.getMinutes();
                var watched=response[i].reader||0;
                var preview=decodeURIComponent(response[i].preview)||'空空如也';

                /*赋值*/
                $(target).find('h3 a').html(title).attr('href','strategyInfo.html?id='+strategyId);
                $(target).find('.author-headImg img').attr('src',headImg);
                $(target).find('.authorName').html(authorName);
                $(target).find('.time').html(time);
                $(target).find('.watched').html(watched);
                $(target).find('.content-body').html(preview);
            }
        }else{
            $('.strategy-list').append('没有内容');
        }

    });
}

//分页
function page(pageNum,pageNo){
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
            $('.active').before("<li><a href='#'>...</a></li>")
                .before("<li><a href='#' page='"+(pageNo-3)+"'>"+(pageNo-3)+"</a></li>")
                .before("<li><a href='#' page='"+(pageNo-2)+"'>"+(pageNo-2)+"</a></li>")
                .before("<li><a href='#' page='"+(pageNo-1)+"'>"+(pageNo-1)+"</a></li>");
        }else{
            for(var i=1;i<pageNo;i++){
                $('.active').before("<li><a href='#' page='"+i+"'>"+i+"</a></li>");
            }
        }
        if(pageNum-pageNo>3){
            $('.active').after("<li><a href='#'>...</a></li>")
                .after("<li><a href='#' page='"+(pageNum)+"'>"+(pageNum)+"</a></li>")
                .after("<li><a href='#' page='"+(pageNum-1)+"'>"+(pageNum-1)+"</a></li>")
                .after("<li><a href='#' page='"+(pageNum-2)+"'>"+(pageNum-2)+"</a></li>");
        }else{
            for(i=pageNum;i>pageNo;i--){
                $('.active').after("<li><a href='#' page='"+i+"'>"+i+"</a></li>");
            }
        }
    }
    getDocumentList(pageNo);
}