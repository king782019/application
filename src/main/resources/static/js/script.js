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
                toastr.success("Accounts removed!");
            }

        })
    });

    $("#removeGoogle").click(function () {
       $.ajax({
           url: '/removeGoogle',
           type: 'POST',
           contentType: "application/json",
           success: function() {
               location.reload();
           }
       })
    });

    $("#removeDropbox").click(function () {
        $.ajax({
            url: '/removeDropbox',
            type: 'POST',
            contentType: "application/json",
            success: function() {
                location.reload();
            }
        })
    });

    $("#removeBox").click(function () {
        $.ajax({
            url: '/removeBox',
            type: 'POST',
            contentType: "application/json",
            success: function() {
                location.reload();
            }
        })
    });

    $("#removeOnedrive").click(function () {
        $.ajax({
            url: '/removeOnedrive',
            type: 'POST',
            contentType: "application/json",
            success: function() {
                location.reload();
            }
        })
    });

    $("#removeYandex").click(function () {
        $.ajax({
            url: '/removeYandex',
            type: 'POST',
            contentType: "application/json",
            success: function() {
                location.reload();
            }
        })
    });

    $("#removeHidrive").click(function () {
        $.ajax({
            url: '/removeHidrive',
            type: 'POST',
            contentType: "application/json",
            success: function() {
                location.reload();
            }
        })
    });

    $("#removePcloud").click(function () {
        $.ajax({
            url: '/removePcloud',
            type: 'POST',
            contentType: "application/json",
            success: function() {
                location.reload();
            }
        })
    });

    $("#sync").click(function() {
        $.ajax({
            url: '/sync',
            type: 'POST',
            contentType: 'application/json',
            success: function() {
                toastr.info("Synchronizing have started!");
            }
        })
    })

    $("#startWorker").click(function () {
        $.ajax({
            url: '/start',
            type: 'POST',
            contentType: 'application/json',
            success: function() {
                toastr.success("Successful start!");
            }
        })
    });

    $("#stopWorker").click(function () {
        toastr.success("Worker will stop soon!");
        $.ajax({
            url: '/stop',
            type: 'POST',
            contentType: 'application/json',
            success: function() {
                console.log("successful stop");

            }
        })
    });

    $("#chechStatus").click(function () {
        $.ajax({
            url: '/status',
            type: 'POST',
            success: function(res) {
                toastr.info(res.toString())
            }
        })
    })

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

                            toastr.success("Google drive was added!");

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

                            toastr.success("DropBox was added!");
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
                            toastr.success("OneDrive was added!");
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
                            toastr.success("Box was added!");
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
                            toastr.success("WebDAV account was added!");
                        }
                    })
                    break;
                default:
                    toastr.error("Provider not found!");
                    break;
            }


        });
    })

});


