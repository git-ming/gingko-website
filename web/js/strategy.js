$(function(){

//页面初始化
    var data={
        page:1
    };
    getDocumentListSize(data);

    //按作者筛选
    $('.search').click(function(){
        var searchVal=$('.searchVal').val()||null;
        data.author=searchVal;
        data.page=1;
        getDocumentListSize(data);
    });

    //按类型筛选
    $('.filterByType').on('click','a',function(){
        $('.filterByType span').removeClass('active');
        data.type=$(this).parent().addClass('active').attr('data-type');
        data.page=1;
        getDocumentListSize(data);
    });

    //按时间筛选
    $('.filterByTime').on('click','a',function(){
        $('.filterByTime span').removeClass('active');
        data.to=new Date().getTime();
        var dataTime=$(this).parent().addClass('active').attr('data-time');
        switch(dataTime){
            case 'day':
                data.from=data.to-86400000;
                break;
            case 'week':
                data.from=data.to-604800000;
                break;
            case 'month':
                data.from=data.to-86400000*30;
                break;
            default :
                data.from=null;
                data.to=null;
                break;
        }
        data.page=1;
        getDocumentListSize(data);
    });

    //分页
    $('.pagination').on('click','a',function(){
        var pageNum=$('.pagination a:last').attr('page');
        data.page=$(this).attr('page');
        page(pageNum,data);
    });
});

//获取攻略列表
function getDocumentList(data){
    $('.strategy-list').empty();
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
                var time=transformDate(response[i].time.time);
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

//获取攻略总页数
function getDocumentListSize(data){
    ajaxRequest('/getDocumentListSize',data,function(response){
        var pageNum=response.return;
        page(pageNum,data);
    });
}

//分页
function page(pageNum,data){
    var pageNo=parseInt(data.page);
    if(pageNum==1){
        $('.pagination').empty();
    }else{
        console.log(pageNum);
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
    getDocumentList(data);
}