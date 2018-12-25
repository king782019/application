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

    $("#removeAccounts").click(function () {
        $.ajax({
            url: '/remove',
            type: 'POST',
            contentType: 'application/json',
            success: function() {
                $(".status").text("Accounts removed");
            }

        })
    })

    $("#startWorker").click(function () {
        $.ajax({
            url: '/start',
            type: 'POST',
            contentType: 'application/json',
            success: function() {
                $(".status").text("Successful start");
            }
        })
    });

    $("#stopWorker").click(function () {
        $.ajax({
            url: '/stop',
            type: 'POST',
            contentType: 'application/json',
            success: function() {
                console.log("successful stop");
                $(".status").text("Successful stop");
            }
        })
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


