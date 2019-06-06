(function(window, document) {
    'use strict';

    function Message() {
        this.timer = null;
    }


    Message.prototype = {
        parentDocument: function() {
            return window.parent.document || window.document;
        },
        idNode: function(id) {
            return this.parentDocument().getElementById(id);
        },
        eleNode: function(ele) {
            return this.parentDocument().getElementsByTagName(ele)[0];
        },
        getEvent: function(event) {
            return event || window.event;
        },
        createEle: function() {
            var params = arguments;
            var newEle = document.createElement(params[0]);
            if (params.length >= 4) {
                newEle.className = params[1];
                newEle.id = params[2];
                if (params.length === 5 && params[3].indexOf('_') != -1) {
                    var t = document.createTextNode(params[4]);
                    newEle.appendChild(t);
                };
                if (params[3].indexOf('_') === -1) {
                    newEle.style.backgroundColor = params[4];
                    this.eleNode(params[3]).appendChild(newEle);
                } else {
                    this.idNode(params[3]).appendChild(newEle);
                };
            };

        },
        bindEventOfID: function(bindEle, bindType, fn) {
            var idEle = this.idNode(bindEle);
            if (idEle.addEventListener) {
                idEle.addEventListener(bindType, fn, false);
            } else if (id.attachEvent) {
                idEle.attachEvent("on" + bindType, fn);
            }
        },
        stopEvent: function(event) {
            var event = this.getEvent(event);
            if (event.preventDefault) {
                return event.preventDefault();
            } else {
                return event.returnValue = false;
            }
        },
        stopBubble: function(event) {
            var event = this.getEvent(event);
            if (event.stopPropagation) {
                event.stopPropagation();
            } else {
                event.cancelBubble = true;
            }
        },
        removeEle: function(clickEleID, removeEle, animationStyle, timer, cancel) {
            var that = this;
            this.bindEventOfID(clickEleID, "click", function(event) {
                // if (confirm) {
                //     confirm();
                // }
                if (cancel) {
                    cancel();
                }

                var clickParentClassName = that.idNode(clickEleID).parentNode;
                var parentClassNameArray = clickParentClassName.className.split(' ');
                parentClassNameArray.length > 1 ? clickParentClassName.className = parentClassNameArray[0] + " " + animationStyle : parentClassNameArray.className = clickParentClassName;
                setTimeout(function() {
                    var node = that.idNode(removeEle);
                    node.parentNode.removeChild(node);
                    that.stopBubble(event);
                }, timer);
            });
        },
        timeoutRemoveEle: function(removeEle, animationStyle, timer) {
            var clickParentClassName = this.idNode(removeEle);
            var parentClassNameArray = clickParentClassName.className.split(' ');
            parentClassNameArray.length > 1 ? clickParentClassName.className = parentClassNameArray[0] + " " + animationStyle : parentClassNameArray.className = clickParentClassName;
            setTimeout(function() {
                clickParentClassName.parentNode.removeChild(clickParentClassName);
            }, timer);
        },
        hideEle: function(clickEle, hideEle, cancel) {
            var that = this;
            this.bindEventOfID(clickEle, "click", function() {
                // if (confirm) {
                //     confirm();
                // }
                if (cancel) {
                    cancel()
                }

                that.idNode(hideEle).setAttribute("style", "display:none");

            })
        }
    };


    Message.prototype.showMode = function(modeID, title, cancel) {
        var myMode = this.idNode(modeID);
        if (myMode === null) {
            console.log('是不是忘加mode节点或id写错了^_^');
            return;
        }
        var model = this.idNode(modeID + "_mode_box");
        if (model != null) {
            model.setAttribute("style", "display:block");
            return;
        }
        myMode.setAttribute("style", "display:block;");
        this.createEle("div", "mode-box", modeID + "_mode_box", "body");
        this.createEle("div", "mode-content", modeID + "_mode_content", modeID + "_mode_box");
        this.createEle("div", "mode-title", modeID + "_mode_title", modeID + "_mode_content", title);
        var modeContent = this.idNode(modeID + "_mode_content");
        modeContent.appendChild(myMode);
        this.createEle("div", "mode-bottom", modeID + "_mode_bottom", modeID + "_mode_content");
        //this.createEle("button", "mode-cancel", modeID + "_mode_cancel", modeID + "_mode_bottom", "取 消");
        this.createEle("button", "mode-submit", modeID + "_mode_cancel", modeID + "_mode_bottom", "取 消");
        // this.createEle("button", "mode-submit", modeID + "_mode_submit", modeID + "_mode_bottom", "确 定");

        // this.hideEle(modeID + "_mode_submit", modeID + "_mode_box", confirm);
        this.hideEle(modeID + "_mode_cancel", modeID + "_mode_box", cancel);
    };



    window.message_box = new Message();

})(window, document);
