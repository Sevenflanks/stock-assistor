/** 共用的list ajax查詢objec */
function defaultListAjax(selector, settings) {
  let defaultSettings = {
    data: function (oData) { // 送回server端前的資訊處理
      enblock();
      let data = selector.toObject();
      data.page = oData.start / oData.length; // 目前第幾頁
      data.size = oData.length; // 一頁長度
      if (oData.order.length > 0) {
        let order = oData.order[0];
        data.sort = oData.columns[order.column].name + ',' + order.dir; // 排序方式
        data.order = JSON.stringify([[order.column, order.dir]]); // 為了做到記憶查詢
      }
      return data;
    },
    dataFilter: function (data) { // server產出資料的資訊處理
      deblock();
      let json = $.parseJSON(data);
      if (!json.messagesEmpty) {
        alertMessages(json.messages, '');
      }
      if (json.totalElements == null) {
        json.totalElements = 0;
      }
      json.recordsTotal = json.totalElements; // 總比數
      json.recordsFiltered = json.totalElements; // 無用, 總之給總比數
      return JSON.stringify(json);
    },
    error: function (jqXHR, textStatus, errorThrown) { // 直接當參數設定的話沒有fail可用, 我也是情非得已
      deblock();
      if (jqXHR.status === 0) {
        msg.error("連線異常, 無法進行查詢, 請檢查您的網路環境");
      } else if (jqXHR.status === 401) {
        msg.error("您的登入已經失效, 稍後為您轉導回登入畫面", null, {
          onHidden: function () {
            window.location.assign('/index.html');
          }
        });
      } else if (jqXHR.status === 403) {
        msg.error("您的帳號沒有權限操作此功能, 請嘗試自行重新登入或聯絡系統管理員");
      } else {
        msg.error("功能發生預期外的錯誤, 請嘗試重新進行操作或聯絡系統管理員");
      }
    }
  };
  $.extend(defaultSettings, settings); // 套用客製化設定
  return defaultSettings;
}


/** 處理畫面block */
function enblock($target) {
  if ($target) {
    $target.find('.ui.auto.dimmer').not('.visible').addClass('active');
  } else {
    $('.ui.auto.dimmer').not('.visible').addClass('active');
  }
  $('form').addClass('loading');
  $('.form').addClass('loading');
  $('button').not('.dropdown, .item').addClass('loading');
  $('.button').not('.dropdown, .item').addClass('loading');
}

function deblock(target) {
  $('form').removeClass('loading');
  $('.form').removeClass('loading');
  $('button').removeClass('loading');
  $('.button').removeClass('loading');
  $('.ui.auto.dimmer').not('.visible').removeClass('active');
}

/** 將form轉為物件以供查詢 */
function toObject(selector, mapping) {
  var obj = form2js($(selector).attr('id'), '.', true, null, true);
  if (mapping) {
    mapping(obj)
  }
  return obj;
}

/** 將form轉為json */
function toJson(selector, mapping) {
  return JSON.stringify(toObject(selector, mapping));
}

let confirmBoxTpl = `
<div class="ui small modal">
	<div class="ui icon header">
		<i class="warning sign icon"></i>
	</div>
	<div class="content">
	</div>
	<div class="actions">
		<div class="ui red basic cancel button">
			<i class="remove icon"></i> No
		</div>
		<div class="ui green ok button">
			<i class="checkmark icon"></i> Yes
		</div>
	</div>
</div>`;

/** MsgBox功能 */
let msg = {
  info: function (message, messages, settings) {
    showMsg('info', message, messages, settings);
  },
  success: function (message, messages, settings) {
    showMsg('success', message, messages, settings);
  },
  warn: function (message, messages, settings) {
    showMsg('warning', message, messages, settings);
  },
  error: function (message, messages, settings) {
    showMsg('error', message, messages, {
      closeButton: true,
      timeOut: 0,
      extendedTimeOut: 0,
      ...settings});
  },
  confirm: function (message, messages, callback, onHidden) {
    showConfirm($(confirmBoxTpl), message, messages, callback, onHidden);
  },
  announce: function (type, message, messages, settings) {
    toastr.options = {
      closeButton: true,
      debug: false,
      newestOnTop: true,
      progressBar: true,
      positionClass: "toast-top-center",
      preventDuplicates: false,
      showDuration: "300",
      hideDuration: "1000",
      timeOut: 0,
      extendedTimeOut: 0,
      showEasing: "swing",
      hideEasing: "linear",
      showMethod: "fadeIn",
      hideMethod: "fadeOut",
      tapToDismiss: false,
    };
    $.extend(toastr.options, settings); // 套用客製化設定
    if (messages && messages.length) {
      if (messages instanceof Array) {
        toastr[type](messages.map(function (m) {
          return '·' + m
        }).join('<br>'), message);
      } else {
        toastr[type](messages, message);
      }
    } else {
      toastr[type](message);
    }
  }
};

/** 顯示訊息 */
function showMsg(type, message, messages, settings) {
  toastr.options = {
    closeButton: false,
    debug: false,
    newestOnTop: true,
    progressBar: true,
    positionClass: "toast-bottom-full-width",
    preventDuplicates: false,
    showDuration: "300",
    hideDuration: "1000",
    timeOut: "5000",
    extendedTimeOut: "1000",
    showEasing: "swing",
    hideEasing: "linear",
    showMethod: "fadeIn",
    hideMethod: "fadeOut",
    onclick: null
  };
  $.extend(toastr.options, settings); // 套用客製化設定
  if (messages && messages.length) {
    if (messages instanceof Array) {
      toastr[type](messages.map(function (m) {
        return '·' + m
      }).join('<br>'), message);
    } else {
      toastr[type](messages, message);
    }
  } else {
    toastr[type](message);
  }
}

/** 顯示訊息 */
function showConfirm($target, message, messages, callback, onHidden) {
  $target.attr('id', '');
  // 主要訊息
  $target.find('.header').append($('<p></p>').html(message));
  // 列表訊息
  if (messages && messages.length > 0) {
    var $list = $('<ul class="list"></ul>');
    messages.forEach(function (msg) {
      $list.append($('<li></li>').html(msg));
    });
    $target.find('.content').append($list);
  }

  $target.modal({
    allowMultiple: true,
    closable: false,
    silent: true,
    onDeny: function () {
      callback(false);
    },
    onApprove: function () {
      callback(true);
    },
    onHidden: function () {
      $target.remove();
      if (onHidden) {
        onHidden();
      }
    }
  });

  // 初始化
  $('body').append($target);
  $target.modal('show');
}

/** 關閉MsgBox */
function closeMsg($target) {
  $target.modal('hide');
}

function winNotify(msg) {
  if ("Notification" in window) {
    Notification.requestPermission().then(permission => {
      if (permission === 'granted') {
        new Notification(msg);
      }
    });
  }
}
