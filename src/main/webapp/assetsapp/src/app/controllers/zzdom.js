import axios from 'axios';
import { prop, forEach } from 'ramda';
import moment from 'moment';

const byId = id => document.getElementById(id);
const bySelector = selector => document.querySelectorAll(selector);

/**
 * 根据form表单的id获取表单下所有可提交的表单数据，封装成数组对象
 */
const getFormData = formId => {
  function serializeArray(form) {
    var field,
      l,
      s = [];
    if (typeof form == 'object') {
      var len = form.elements.length;
      for (var i = 0; i < len; i++) {
        field = form.elements[i];
        if (
          field.name &&
          !field.disabled &&
          field.type != 'file' &&
          field.type != 'reset' &&
          field.type != 'submit' &&
          field.type != 'button'
        ) {
          if (field.type == 'select-multiple') {
            l = form.elements[i].options.length;
            for (j = 0; j < l; j++) {
              if (field.options[j].selected)
                s[s.length] = {
                  name: field.name,
                  value: field.options[j].value
                };
            }
          } else if (
            (field.type != 'checkbox' && field.type != 'radio') ||
            field.checked
          ) {
            s[s.length] = { name: field.name, value: field.value };
          }
        }
      }
    }
    return s;
  }

  var data = {};

  var results = serializeArray(byId(formId));

  const extractItem = item => {
    //文本表单的值不为空才处理
    if (item.value && $.trim(item.value) !== '') {
      item.value = $.trim(item.value);

      if (!data[item.name]) {
        data[item.name] = item.value;
      } else {
        //name属性相同的表单，以 空格 拼接
        data[item.name] = data[item.name] + ' ' + item.value;
      }
    }
  };

  forEach(extractItem, results);

  return data;
};

// select 后面紧跟一个 hidden，在 hidden 中有 select 的值
const initSelectOptions = formId => {
  const items = bySelector(`#${formId} select`);

  const mark = dom => {
    const v = dom.nextElementSibling.value;
    if (v === undefined || v.length === 0) {
      return;
    }

    dom.value = v;
  };

  forEach(mark)(items);
};

const removeClass = cls => dom => {
  dom.classList.remove(cls);
};

const addClass = cls => dom => {
  dom.classList.add(cls);
};

const setDisplay = display => dom => {
  dom.style.display = display;
};

const setHtml = html => dom => {
  dom.innerHTML = html;
};

const setHtmlUrl = url => dom => {
  axios
    .get(url)
    .then(prop('data'))
    .then(html => setHtml(html)(dom));
};

// 进入到新的页面
const visit = url => {
  Turbolinks.visit(url);
};

const dateFormat = 'YYYY-MM-DD';
// range 固定格式: "2019-11-21 - 2019-12-22"
const splitRange = range => {
  let rangeStart = '';
  let rangeEnd = '';

  if (range === undefined || range.length === 0) {
    return { rangeStart, rangeEnd };
  }

  rangeStart = range.substring(0, 10);
  rangeEnd = range.substring(13);

  return { rangeStart, rangeEnd };
};

// 返回从今天开始，过去的 n_months，默认过去 1 个月
const genRange = n_months => {
  if (n_months === undefined) {
    n_months = 1;
  }

  const start = moment()
    .add(-1 * n_months, 'months')
    .toDate();

  const end = moment()
    .add(1, 'days')
    .toDate();

  return { start, end };
};

export default {
  byId,
  bySelector,

  getFormData,
  initSelectOptions,

  visit,
  dateFormat,
  splitRange,
  genRange,

  removeClass,
  addClass,
  setDisplay,
  setHtml,
  setHtmlUrl
};
