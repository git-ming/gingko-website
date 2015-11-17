/**
 * Created by Administrator on 2015/10/17.
 */
$(function(){

    //攻略标签页
    var strategyData={};
    strategyData.type=$('#strategy .strategy-tab ul a').eq(0).attr('aria-controls');
    getDocumentList(strategyData);
    $('.strategy-tab a').click(function (e) {
        e.preventDefault();
        $(this).tab('show');
        strategyData.type=$(this).attr('aria-controls');
        getDocumentList(strategyData);
    });


});

//获取攻略列表
function getDocumentList(data){
    var type=data.type;
    ajaxRequest('/getDocumentList',data,function(response){
        var strategyNum=response.length;
        if(strategyNum>0){

            //首个
            var first=$('#strategy '+type+' tab-content-left');
            console.log($(first).find('p').html());
            $(first).find('p').html(decodeURIComponent(response[0].title));
            console.log(response[0].title);
            console.log($(first).find('p').html());
            $(first).find('a').attr('href','strategyInfo.html?id='+response[0].id);
            $(first).find('small').html(response[0].preview);

            for(var i=1;i<strategyNum;i++){
                var target=$('#strategy '+type+' tab-item').eq(i-1);
                /*判空过滤*/
                var strategyId=response[i].id||0;
                var title=decodeURIComponent(response[i].title)||'无标题';
                //var headImg=response[i].headImg||'../images/logo.png';
                //var authorName=decodeURIComponent(response[i].author)||'admin';
                //var time=transformDate(response[i].time.time);
                //var watched=response[i].reader||0;
                var preview=decodeURIComponent(response[i].preview)||'空空如也';
                //var author=response[i].author||null;

                /*赋值*/
                $(target).find('p').html(title);
                $(target).find('a').attr('href','strategyInfo.html?id='+strategyId);
                //$(target).find('.author-headImg a').attr('href','space.html?user='+author);
                //$(target).find('.author-headImg img').attr('src',headImg);
                //$(target).find('.authorName').html(authorName);
               // $(target).find('.time').html(time);
                //$(target).find('.watched').html(watched);
                $(target).find('small').html(preview);

            }
        }
    });
}
