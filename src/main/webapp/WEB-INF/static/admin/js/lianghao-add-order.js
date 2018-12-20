$(function() {
	$("#addOrder").click(function () {
		$.post("lianghao/add-order",$("#form").serialize(),function (result) {
			console.log(result);
			if(result.code == 200) {
                alert("下单成功");
			}else {
				alert(result.data);
			}
			refresh("lianghao/lianghao-query");
        })
    })
});

