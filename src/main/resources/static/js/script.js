$(document).ready(function () {

    var obj = {
        source: 0,
        destination: 0
    }

    $("#s1, #s2, #s3, #s4").change(function () {
        obj.source = $("input[name=sourceGroup]:checked").val();
    });

    $("#d1, #d2, #d3, #d4").change(function () {
        obj.destination = $("input[name=destinationGroup]:checked").val();
    });

    $(".createTableGoogle").click(function () {
        $(".tableView tbody tr").remove();
        $(".tableView thead").loading();
        $.ajax({
            url: '/getFilesGoogle',
            type: 'GET',
            contentType: 'application/json',
            dataType: 'json',
            success: function (res) {
                $(".tableView tbody tr").remove();
                $(".tableView thead").loading('stop');
                var i = 0;
                for (; i < res.objects.length; i++) {
                    if (res.objects[i].type === "file") {
                        $(".tableView tbody").append("<tr><td>" + res.objects[i].name + "</td></tr>")
                    }
                }
            }
        })
    });

    $(".createTableDropbox").click(function () {
        $(".tableView tbody tr").remove();
        $(".tableView thead").loading();
        $.ajax({
            url: '/getFilesDropbox',
            type: 'GET',
            contentType: 'application/json',
            dataType: 'json',
            success: function (res) {
                $(".tableView tbody tr").remove();
                $(".tableView thead").loading('stop');
                var i = 0;
                for (; i < res.objects.length; i++) {
                    if (res.objects[i].type === "file") {
                        $(".tableView tbody").append("<tr><td>" + res.objects[i].name + "</td></tr>")
                    }
                }
            }
        })
    });

    $(".createTableOnedrive").click(function () {
        $(".tableView tbody tr").remove();
        $(".tableView thead").loading();
        $.ajax({
            url: '/getFilesOnedrive',
            type: 'GET',
            contentType: 'application/json',
            dataType: 'json',
            success: function (res) {
                $(".tableView tbody tr").remove();
                $(".tableView thead").loading('stop');
                var i = 0;
                for (; i < res.objects.length; i++) {
                    if (res.objects[i].type === "file") {
                        $(".tableView tbody").append("<tr><td>" + res.objects[i].name + "</td></tr>")
                    }
                }
            }
        })
    });

    $(".createTableBox").click(function () {
        $(".tableView tbody tr").remove();
        $(".tableView thead").loading();
        $.ajax({
            url: '/getFilesBox',
            type: 'GET',
            contentType: 'application/json',
            dataType: 'json',
            success: function (res) {
                $(".tableView tbody tr").remove();
                $(".tableView thead").loading('stop');
                var i = 0;
                for (; i < res.objects.length; i++) {
                    if (res.objects[i].type === "file") {
                        $(".tableView tbody").append("<tr><td>" + res.objects[i].name + "</td></tr>")
                    }
                }
            }
        })
    });



    $("#sync").click(function () {
        $(".message").text("")

        if (obj.source != 0 && obj.destination != 0) {
            if (obj.source == obj.destination) {
                $(".message").text("Source and destination are same");
            } else {
                $(".message").loading();
                $.ajax({
                    url: '/sync',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(obj),
                    dataType: 'json',
                    success: function (data) {
                        $(".message").loading('stop');
                        $(".message").text("Synchronized");

                    },
                })
            }
        } else {
            $(".message").text("Source or destination have not been chosen");
        }
    });

    $("#twowaysync").click(function () {
        $(".message").text("")
        if (obj.source != 0 && obj.destination != 0) {
            if (obj.source == obj.destination) {
                $(".message").text("Source and destination are same");
            } else {
                $(".message").loading();
                $.ajax({
                    url: '/twowaysync',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(obj),
                    dataType: 'json',
                    success: function (data) {
                        $(".message").loading('stop');
                        $(".message").text("Synchronized");

                    },
                })
            }
        } else {
            $(".message").text("Source or destination have not been chosen");
        }
    });

    $(function () {
        window.Kloudless.authenticator($("#test"), {
            client_id: 'UOgAgpNDvRVzJTE_3MeomH7bFsply7ZoWV5EAdRmsNCRbanW',
        }, function (res) {

            $(".sync").text("");
            switch (res.account.service) {
                case "gdrive":
                    $.ajax({
                        url: '/addServiceGoogle',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(res),
                        dataType: 'json',
                        success: function (data) {

                            $(".sync").text("Added");

                        },
                    });
                    break;
                case "dropbox":
                    $.ajax({
                        url: '/addServiceDropbox',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(res),
                        dataType: 'json',
                        success: function (data) {

                            $(".sync").text("Added");
                        },
                    });
                    break;
                case "skydrive":
                    $.ajax({
                        url: '/addServiceOnedrive',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(res),
                        dataType: 'json',
                        success: function (data) {

                            $(".sync").text("Added");
                        },
                    });
                    break;
                case "box":
                    $.ajax({
                        url: '/addServiceBox',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(res),
                        dataType: 'json',
                        success: function (data) {

                            $(".sync").text("Added");
                        },
                    });
                    break;
                default:
                    $(".sync").text("Error provider not found!");
                    break;
            }


        });
    })

});


