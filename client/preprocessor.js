 //preprocessor.js
//var ReactTools = require('react-tools');
//module.exports = {
//  process: function(src) {
//    return ReactTools.transform(src, { harmony: true });
//  }
//};

var ReactTools = require('react-tools');
var to5 = require('6to5-jest').process;

module.exports = {
  process: function(src, filename) {
    return ReactTools.transform(to5(src, filename));
  }
};