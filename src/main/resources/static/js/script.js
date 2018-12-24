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


