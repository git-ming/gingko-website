/**
 * Created by Administrator on 2015/11/5.
 */
$(function(){
    var aimUser=getQueryString('user');
    otherUserInfo(aimUser);

    //关注
    isMarked(aimUser);
    $('.focus').click(function(){
        if($(this).html()=='关注'){
            mark(encodeURIComponent(aimUser));
        }else if($(this).html()=='取消关注'){
            unMark(encodeURIComponent(aimUser));
        }
    });

    //私信
    $('.aim').html(aimUser);
    $('.submit').click(function(){
        var message=$('.chatMessage').val();
        var preview=message.substr(0,50);
        sendMessage(aimUser,decodeURIComponent(message),preview);
    });
    getDocumentList(aimUser);
});
//获取任意用户消息
function otherUserInfo(aimUser){
    var data={
        aimUser:aimUser
    };
    ajaxHeader('/otherUserInfo',data,function(response){
        var userName=decodeURIComponent(response.username);
        var headImgPath=response.headImg||'../images/headImg.jpg';

        $('.nickName').html(userName);
        $('.headImg img').attr('src',headImgPath);
    });
}

//获取攻略列表
function getDocumentList(aimUser){
    $('.timeLine').empty();
    var data={
        author:aimUser
    };
    ajaxRequest('/getDocumentList',data,function(response){
        var strategyNum=response.length;
        console.log(strategyNum);
        if(strategyNum>0){
            for(var i=0;i<strategyNum;i++){
                $('.timeLine').append($('#timeLine').html());
                var target=$('.timeLine li').eq(i);
                /*判空过滤*/
                var strategyId=response[i].id||0;
                var title=decodeURIComponent(response[i].title)||'无标题';
                var headImg=response[i].headImg||'../images/logo.png';
                var authorName=decodeURIComponent(response[i].author)||'admin';
                var date=new Date(response[i].time.time)||new Date();
                var year=date.getFullYear();
                var month=date.getMonth()+1;
                var day=date.getDate();
                var watched=response[i].reader||0;
                var preview=decodeURIComponent(response[i].preview)||'空空如也';
                var author=response[i].author||null;

                /*赋值*/
                $('.timeLine-date dt').html(month+'/'+day);
                $('.timeLine-date dd').html(year);
                $(target).find('h3 a').html(title).attr('href','strategyInfo.html?id='+strategyId);
                $(target).find('.watched').html(watched);
                $(target).find('.timeLine-intro').html(preview);

            }
        }else{
            /*$('.timeLine').append($('#timeLine').html());
            $('.timeLine li')*/
        }

    });
}


//判断是否关注
function isMarked(aimUser){
    var data={
        aimUser:aimUser
    };
    ajaxHeader('/isMarked',data,function(response){
        if(response.return){   //已关注
            $('.mark').html('取消关注');
        }else{                 //未关注
            $('.mark').html('关注');
        }
    });
}

//关注
function mark(aimUser){
    var data={
        aimUser:aimUser
    };
    ajaxHeader('/mark',data,function(){
        $('.mark').html('取消关注');
    });
}

//取消关注
function unMark(aimUser){
    var data={
        aimUser:aimUser
    };
    ajaxHeader('/unMark',data,function(){
        $('.mark').html('关注');
    });
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
