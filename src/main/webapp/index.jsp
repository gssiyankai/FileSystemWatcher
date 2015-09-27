<!DOCTYPE html>
<html>
<head>
<title>File System Watcher</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css">
<link rel="stylesheet" href="/css/styles.css">
<script src="http://ajax.aspnetcdn.com/ajax/jquery/jquery-1.9.0.js"></script>
<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>
<script src="http://ajax.aspnetcdn.com/ajax/knockout/knockout-2.2.1.js"></script>
</head>
<body>
    <div class="navbar">
        <div class="navbar-inner">
            <a class="brand" href="#">File System Watcher</a>
        </div>
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
        <button data-bind="click: beginAdd" class="btn">Add Item</button>
    </div>

    <div id="add" class="modal hide fade" tabindex="=1" role="dialog" aria-labelledby="addDialogLabel" aria-hidden="true">
        <div class="modal-header">
            <h3 id="addDialogLabel">Add Item</h3>
        </div>
        <div class="modal-body">
            <form class="form-horizontal">
                <div class="control-group">
                    <label class="control-label" for="inputPath">Path</label>
                    <div class="controls">
                        <input data-bind="value: path" type="text" id="inputPath" style="width: 300px;">
                    </div>
                </div>
                <span class="btn btn-default btn-file">Browse...<input type="file"></span>
            </form>
        </div>
        <div class="modal-footer">
            <button data-bind="click: addItem" class="btn btn-primary">Add Item</button>
            <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
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
                    self.ajax(self.watchURI, 'POST', path);
                }
            }

            function AddItemViewModel() {
                var self = this;
                self.path = ko.observable();

                self.addItem = function() {
                    $('#add').modal('hide');
                    itemsViewModel.add(self.path());
                    self.path("");
                }
            }

            var itemsViewModel = new ItemsViewModel();
            var addItemViewModel = new AddItemViewModel();
            ko.applyBindings(itemsViewModel, $('#main')[0]);
            ko.applyBindings(addItemViewModel, $('#add')[0]);

    </script>
</body>
</html>