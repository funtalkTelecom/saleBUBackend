$(function() {
	$("#addOrder").click(function () {
		$.post("lianghao/add-order",$("#form").serialize(),function (result) {
			alert("下单成功");
			refresh("lianghao/lianghao-query");
        })
    })
});

