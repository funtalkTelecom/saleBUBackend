
var setting = {
    check: {
        autoCheckTrigger:true,
        chkStyle:"checkbox",
        enable: true,
        chkboxType: {"Y":"ps", "N":"ps"}
    },
    view: {
        dblClickExpand: false
    },
    data: {
        simpleData: {
            enable: true
        }
    },
    callback: {
        beforeClick: beforeClick,
        onCheck: onCheck
    }
};
function beforeClick(treeId, treeNode) {
    var zTree = $.fn.zTree.getZTreeObj(treeId);
    zTree.checkNode(treeNode, !treeNode.checked, null, true);
    if(treeNode.isParent) {//判断是否为父节点
        var childs = treeNode.children;
        for(var i=0;i<childs.length;i++){
            zTree.checkNode(childs[i],treeNode.checked, null, true);
        }
    }
    return false;
}

function onCheck(e, treeId, treeNode) {
    var zTree = $.fn.zTree.getZTreeObj(treeId),
        nodes = zTree.getCheckedNodes(true),
        v = "", vv = "";
    for (var i=0, l=nodes.length; i<l; i++) {
        if (!nodes[i].isParent) {
            v += nodes[i].name + ",";
            vv += ","+nodes[i].id;
        }
    }
    if (v.length > 0 ) v = v.substring(0, v.length-1);
    if (vv.length > 0 ) vv += ",";
    var cityStrObj = $("#"+$("#"+treeId).attr("strObj"));
    var cityObj = $("#"+$("#"+treeId).attr("valObj"));
    cityStrObj.val(v);
    cityObj.val(vv);
}

function showCityMenu() {
    var cityObj = $("#gSaleCity");
    var cityOffset = $("#gSaleCity").offset();
    if($("#menuContent").is(":hidden")) $("#menuContent").css({"background-color":"#ffffff", "border":"1px solid #438eb9"}).slideDown("fast");

    $("body").bind("mousedown", onBodyDown);
}
function hideMenu() {
    $("#menuContent").fadeOut("fast");
    $("body").unbind("mousedown", onBodyDown);
}
function onBodyDown(event) {
    if (!(event.target.id == "menuBtn" || event.target.id == "gSaleCity" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
        hideMenu();
    }
}