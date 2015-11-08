<!DOCTYPE html>
<html>
<head>
<title>File System Watcher</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="/css/styles.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/knockout/3.3.0/knockout-min.js"></script>
</head>
<body>
    <nav class="navbar navbar-default">
        <nav class="navbar navbar-default">
          <div class="container-fluid">
            <a class="navbar-brand" href="#">File System Watcher</a>
          </div>
    </nav>

    <div id="credentials" class="container" data-bind="visible: showCredentials">
        <form class="form-horizontal">
            <div class="form-group">
                <label for="inputUsername" class="control-label col-xs-2">Username</label>
                <div class="col-xs-5">
                    <input type="username" class="form-control" id="inputUsername" placeholder="Username" data-bind="value: username">
                </div>
            </div>
            <div class="form-group">
                <label for="inputPassword" class="control-label col-xs-2">Password</label>
                <div class="col-xs-5">
                    <input type="password" class="form-control" id="inputPassword" placeholder="Password" data-bind="value: password">
                </div>
            </div>
            <div class="form-group">
                <div class="col-xs-offset-2 col-xs-10">
                    <div class="checkbox">
                        <label><input type="checkbox" disabled="true"> Remember me</label>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="col-xs-offset-2 col-xs-10">
                    <button type="submit" class="btn btn-primary" data-bind="click: clickLogin">Login</button>
                </div>
            </div>
        </form>
    </div>

    <div id="main" class="container" data-bind="visible: showMain">
        <ul class="nav nav-tabs">
          <li class="active"><a href="#configuration" data-toggle="pill">Configuration</a></li>
          <li><a href="#logs" data-toggle="pill">Logs</a></li>
        </ul>

        <div id='content' class="tab-content">
          <div class="tab-pane active" id="configuration">
            <table class="table table-striped">
                <tr></td><td><b>Item</b></td><td><b>Status</b></td><td><b>Options</b></td></tr>
                <!-- ko foreach: items -->
                <tr>
                    <td>
                        <p data-bind="text: path"></p>
                    </td>
                    <td>
                        <span data-bind="visible: done" class="label label-success">Watching</span>
                    </td>
                    <td>
                        <button data-bind="enable:false" class="btn">Suspend</button>
                        <button data-bind="enable:false" class="btn">Remove</button>
                    </td>
                </tr>
                <!-- /ko -->
            </table>
            <button data-bind="click: clickAddItem" class="btn btn-primary">Add Item</button>
          </div>
          <div class="tab-pane" id="logs">
            <table class="table table-striped">
                 <tr></td><td><b>Time</b></td></td><td><b>Event</b></td></td><td><b>Path</b></td><td><b>Status</b></td></tr>
                 <!-- ko foreach: events -->
                 <tr>
                     <td>
                         <p data-bind="text: time"></p>
                     </td>
                     <td>
                         <p data-bind="text: event"></p>
                     </td>
                     <td>
                         <p data-bind="text: path"></p>
                     </td>
                     <td>
                         <p data-bind="text: status"></p>
                     </td>
                 </tr>
                 <!-- /ko -->
             </table>
          </div>
        </div>
    </div>

    <div id="add" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title">Add item</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal">
                        <div class="control-group">
                            <div class="controls">
                                <label class="control-label" for="inputPath">Path</label>
                                <input class="input-path" type="text" id="inputPath" data-bind="value: inputPath">
                                <span class="btn btn-default btn-file">Browse...<input disabled="disabled" type="file" data-bind="value: inputPath"></span>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button data-bind="click: clickConfirm" class="btn btn-primary" data-dismiss="modal">Confirm</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>

    <script type="text/javascript">

            function ViewModel() {
                var self = this;
                self.restServerURI = 'https://localhost:8443/rest/';
                self.showCredentials = ko.observable(true);
                self.username = ko.observable("");
                self.password = ko.observable("");
                self.showMain = ko.observable(false);
                self.items = ko.observableArray();
                self.inputPath = ko.observable("");
                self.events = ko.observableArray();

                self.itemsWebsocket;
                self.itemsWebsocketURI = 'wss://' + window.location.host + '/websocket/items/notify';

                self.changesWebsocket;
                self.changesWebsocketURI = 'wss://' + window.location.host + '/websocket/changes/notify';

                self.ajax = function(uri, method, data) {
                    var request = {
                        url: uri,
                        type: method,
                        contentType: "application/json",
                        accepts: "application/json",
                        cache: false,
                        dataType: 'json',
                        data: data,
                        error: function(jqXHR) {
                            console.log("ajax error " + jqXHR.status);
                        }
                    };
                    return $.ajax(request);
                }

                self.clickLogin = function() {
                    self.ajax(self.restServerURI + 'login', 'POST', self.username() + ':' + self.password())
                        .done(function(data) {
                            self.showCredentials(false);
                            self.showMain(true);
                            self.listenItems();
                            self.listenChanges();
                        })
                        .fail(function (jqXHR, status, error) {
                            alert(jqXHR.responseText);
                        });
                }

                self.clickAddItem = function() {
                    $('#add').modal('show');
                }

                self.clickConfirm = function() {
                    self.ajax(self.restServerURI + 'watch', 'POST', self.inputPath())
                        .done(function() {
                            self.inputPath("");
                        })
                        .fail(function (jqXHR, status, error) {
                            alert(jqXHR.responseText);
                        });
                }

                self.listenItems = function() {
                    if ('WebSocket' in window) {
                        self.itemsWebsocket = new WebSocket(self.itemsWebsocketURI);
                    } else if ('MozWebSocket' in window) {
                        self.itemsWebsocket = new MozWebSocket(self.itemsWebsocketURI);
                    } else {
                        alert('WebSocket is not supported by this browser.');
                        return;
                    }
                    self.itemsWebsocket.onopen = function(event) {
                        display('Connected to server.');
                    };
                    self.itemsWebsocket.onclose = function() {
                       display('Disconnected from server');
                    };
                    self.itemsWebsocket.onmessage = function(event) {
                        paths = event.data.split(";");
                        for(var i = 0; i < paths.length; ++i) {
                            self.items.push({
                                path: ko.observable(paths[i]),
                                done: ko.observable(true)
                            });
                        }
                    };
                }

                self.listenChanges = function() {
                    if ('WebSocket' in window) {
                        self.changesWebsocket = new WebSocket(self.changesWebsocketURI);
                    } else if ('MozWebSocket' in window) {
                        self.changesWebsocket = new MozWebSocket(self.changesWebsocketURI);
                    } else {
                        alert('WebSocket is not supported by this browser.');
                        return;
                    }
                    self.changesWebsocket.onopen = function(event) {
                        display('Connected to server.');
                    };

                    self.changesWebsocket.onclose = function() {
                       display('Disconnected from server');
                    };
                    self.changesWebsocket.onmessage = function(event) {
                        data = event.data.split(";");
                        self.events.unshift({
                            time: ko.observable(data[0]),
                            event: ko.observable(data[1]),
                            path: ko.observable(data[2]),
                            status: ko.observable("done")
                        });
                    };
                }
            }

            ko.applyBindings(new ViewModel());

    </script>
</body>
</html>