/**
 * Created by Administrator on 2015/10/29.
 */
$(function(){
    $('.nickName').html(decodeURIComponent(sessionStorage.username));
    getMessageList();

    //全部标记为已读
    $('.readAll').click(function(){
        var data = JSON.parse(sessionStorage.messageList);
        ajaxHeader('/readAllMessage',data,function(data){
            $('.message-list li').css('opacity', '0.6');
            $('.messageNum').html('0');
        });
    });

    //标记已读
    $('.message-list').on('click','li',function(){
        var _this=$(this);
        if($(this).css('opacity')==1){
            readMessage(_this);
            getMessage($(this).attr('messageId'));
        }
    }).on('click','input',function(event){   //选中
        event.stopPropagation();
    });

    $('.delete').click(function(){
        var selectArr=[];
        var messageLi=$('.message-list li');
        for(var i=0;i<messageLi.length;i++){
            var isSelected=messageLi.eq(i).find('input').prop('checked');
            if(isSelected){
                selectArr.push({
                    id:messageLi.eq(i).attr('messageId')
                });
            }
        }
        removeMessage(selectArr);
    });
});


//获取消息列表
function getMessageList(){
    ajaxHeader('/getMessageList',null,function(data){
        var messageNum=0;
        var array=[];
        var newData=rankByTime(data);
        for(var i=0;i<newData.length;i++){
            var content=decodeURIComponent(newData[i].preview);
            var author=decodeURIComponent(newData[i].author);
            var date=transformDate(newData[i].time.time);
            $('.message-list').prepend($('#message-template').html());
            var messageNo=$('.message-list li').eq(0);
            messageNo.find('.message-tag').html(newData[i].type+':');
            messageNo.find('.message-content').html(content);
            messageNo.find('.message-author').html('by'+author);
            messageNo.find('.message-date').html(date);
            messageNo.attr('messageId',newData[i].id);
            if(newData[i].read){
                messageNo.css('opacity','0.6');
            }else{
                messageNum+=1;
            }
        }
        sessionStorage.messageList=JSON.stringify(newData);
        $('.messageNum').html(messageNum);
    });
}

//获取关注列表
function getMarkedList(){
    $('.nav-tabs li').removeClass('active');
    $('.myFocus').parent().addClass('active');
    $('.Focus ul').empty();
    $('.Focus').show();
    $('.Messages').hide();
    ajaxHeader('/getMarkedList',null,function(response){
        $('.focusNum').html(response.length);
        for(var i=0;i<response.length;i++){
            $('.focus-list').append("<li class='list-group-item'>1</li>");
            var focusNo=$('.focus-list li').eq(i);
            focusNo.html('用户：'+response[i].to);
        }
    });
}


//读取消息
function readMessage(_this){
    var data={
        id:$(_this).attr('messageId')
    };
    ajaxHeader('/readMessage',data,function(data){
        var messageNum=$('.messageNum');
        $(_this).css('opacity','0.6');
    });
}

//删除消息
function removeMessage(sendData){
    ajaxHeader('/removeAllMessage',sendData,function(data){
        var messageLi=$('.message-list li');
        var length=messageLi.length;
        for(var i=0;i<length;i++){
            var isSelected=messageLi.eq(i).find('input').prop('checked');
            if(isSelected){
                messageLi.eq(i).remove();
            }
        }
    });
}

function getMessage(id){
    var data={
        id:id
    };
    ajaxHeader('/getMessage',data,function(data){
        var info=$('#messageModal');
        var message=decodeURIComponent(data.message);
        $(info).find('.modal-title').html(data.type+'<span class="label label-default">by'+data.author+'</span>');
        $(info).find('.modal-body').html('<p>'+message+'</p>');
    });
}