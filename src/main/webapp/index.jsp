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

    <div id="credentials" class="container">
        <form class="form-horizontal">
            <div class="form-group">
                <label for="inputUsername" class="control-label col-xs-2">Username</label>
                <div class="col-xs-5">
                    <input type="username" class="form-control" id="inputUsername" placeholder="Username">
                </div>
            </div>
            <div class="form-group">
                <label for="inputPassword" class="control-label col-xs-2">Password</label>
                <div class="col-xs-5">
                    <input type="password" class="form-control" id="inputPassword" placeholder="Password">
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
                    <button type="submit" class="btn btn-primary">Login</button>
                </div>
            </div>
        </form>
    </div>

    <div id="main" class="container">
        <table class="table table-striped">
            <tr></td><td><b>Item</b></td><td><b>Status</b></td><td><b>Options</b></td></tr>
            <!-- ko foreach: items -->
            <tr>
                <td>
                    <p data-bind="text: path"></p>
                </td>
                <td>
                    <span data-bind="visible: done" class="label label-success">Done</span>
                    <span data-bind="visible: !done()" class="label label-important">In Progress</span>
                </td>
                <td>
                    <button data-bind="enable:false" class="btn">Suspend</button>
                    <button data-bind="enable:false" class="btn">Remove</button>
                </td>
            </tr>
            <!-- /ko -->
        </table>
        <button data-bind="click: beginAdd" class="btn btn-primary">Add Item</button>
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
                                <input class="input-path"  data-bind="value: path" type="text" id="inputPath">
                                <span class="btn btn-default btn-file">Browse...<input type="file"></span>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button data-bind="click: addItem" type="button" class="btn btn-primary">Confirm</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>

    <script type="text/javascript">

            function ItemsViewModel() {
                var self = this;
                self.watchURI = 'http://localhost:8080/rest/watch';
                self.items = ko.observableArray();

                self.ajax = function(uri, method, data) {
                    var request = {
                        url: uri,
                        type: method,
                        contentType: "application/json",
                        accepts: "application/json",
                        cache: false,
                        dataType: 'json',
                        data: data,
                        beforeSend: function() { alert("Loading...") },
                        error: function(jqXHR) {
                            console.log("ajax error " + jqXHR.status);
                        }
                    };
                    return $.ajax(request);
                }

                self.beginAdd = function() {
                    $('#add').modal('show');
                }

                self.add = function(path) {
                    self.items.push({path: path});
                }
            }

            function AddItemViewModel(itemsViewModel) {
                var self = this;
                self.itemsViewModel = itemsViewModel;
                self.path = ko.observable();

                self.addItem = function() {
                    $('#add').modal('hide');
                    self.itemsViewModel.add(self.path());
                    self.path("");
                }
            }

            var itemsViewModel = new ItemsViewModel();
            var addItemViewModel = new AddItemViewModel(itemsViewModel);
            ko.applyBindings(itemsViewModel, $('#main')[0]);
            ko.applyBindings(addItemViewModel, $('#add')[0]);

    </script>
</body>
</html>