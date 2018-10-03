$(document).ready(function () {

    var obj = {
        source: 0,
        destination: 0
    };

    $("#s1, #s2, #s3, #s4, #s5, #s6, #s7").change(function () {
        obj.source = $("input[name=sourceGroup]:checked").val();
    });

    $("#d1, #d2, #d3, #d4, #d5, #d6, #d7").change(function () {
        obj.destination = $("input[name=destinationGroup]:checked").val();
    });

    $(".btn-status").click(function () {
        checkStatusGoogle();
        checkStatusDropbox();
        checkStatusOnedrive();
        checkStatusBox();

        var hours = new Date().getHours();
        var minutes = new Date().getMinutes();
        var seconds = new Date().getSeconds();
        $(".lastTimeChecked").text("Last time checked: " + hours + ":" + minutes + ":" + seconds);
    });

    function checkStatusGoogle() {
        $.ajax({
            url:'/getStatusGoogle',
            type: 'GET',
            contentType: 'application/json',
            dataType: 'text',
            success: function(res) {
                if(res === "OK") {
                    $(".googleStatus").text("Google:  OK");
                } else {
                    $(".googleStatus").text("Google:  Not found");
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                $(".googleStatus").text("Google:  Not available");
            }
        })
    }

    function checkStatusDropbox() {
        $.ajax({
            url:'/getStatusDropbox',
            type: 'GET',
            contentType: 'application/json',
            dataType: 'text',
            success: function(res) {
                if(res === "OK") {
                    $(".dropboxStatus").text("DropBox:  OK");
                } else {
                    $(".dropboxStatus").text("DropBox:  Not found");
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                $(".dropboxStatus").text("DropBox:  Not available");
            }
        })
    }

    function checkStatusOnedrive() {
        $.ajax({
            url:'/getStatusOnedrive',
            type: 'GET',
            contentType: 'application/json',
            dataType: 'text',
            success: function(res) {
                if(res === "OK") {
                    $(".onedriveStatus").text("OneDrive:  OK");
                } else {
                    $(".onedriveStatus").text("OneDrive:  Not found");
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                $(".onedriveStatus").text("OneDrive:  Not available");
            }
        })
    }

    function checkStatusBox() {
        $.ajax({
            url:'/getStatusBox',
            type: 'GET',
            contentType: 'application/json',
            dataType: 'text',
            success: function(res) {
                if(res === "OK") {
                    $(".boxStatus").text("Box:  OK");
                } else {
                    $(".boxStatus").text("Box:  Not found");
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                $(".boxStatus").text("Box:  Not available");
            }
        })
    }

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

                    $(".tableView tbody").append("<tr><td>" + res.objects[i].name + "</td></tr>")

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
                    $(".tableView tbody").append("<tr><td>" + res.objects[i].name + "</td></tr>")

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

                    $(".tableView tbody").append("<tr><td>" + res.objects[i].name + "</td></tr>")

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

                    $(".tableView tbody").append("<tr><td>" + res.objects[i].name + "</td></tr>")

                }
            }
        })
    });


    $("#sync").click(function () {
        $(".message").text("");

        if (obj.source != 0 && obj.destination != 0) {
            if (obj.source == obj.destination) {
                $(".message").text("Source and destination are same");
            } else {
                $(".message").loading();
                $.ajax({
                    url: '/synco',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(obj),
                    dataType: 'json',
                    success: function (data) {
                        console.log(data);
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
        $(".message").text("");
        if (obj.source != 0 && obj.destination != 0) {
            if (obj.source == obj.destination) {
                $(".message").text("Source and destination are same");
            } else {
                $(".message").loading();
                $.ajax({
                    url: '/twowaysynco',
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
            console.log(res);

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
                case "webdav":
                    $.ajax({
                        url: '/addWebDav',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(res),
                        dataType: 'json',
                        success: function (data) {
                            $(".sunc").text("Added");
                        }
                    })
                default:
                    $(".sync").text("Error provider not found!");
                    break;
            }


        });
    })

});


