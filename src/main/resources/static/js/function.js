let fileId = null;
let fileHistory = null;
let draft = null;
let preContent = null;
let editNumber = null;

function queryDraft() {
    $.ajax({
        url: httpUrl + 'queryDraft',
        dataType: 'json',
        type: 'GET',
        data: {fileId: fileId},
        success: function (data) {
            document.getElementById("draft").value = data.data;
        },
        error: function (data) {

        }
    });
}

function saveDraft() {
    const fileContent = document.getElementById("fileContent").value;
    data = {
        fileId: fileId,
        fileContent: fileContent
    };
    if (draft != fileContent) {
        $.ajax({
            url: httpUrl + 'addDraft',
            dataType: 'json',
            type: 'POST',
            data: data,
            success: function (data) {
                draft = fileContent;
                const date = new Date();
                document.getElementById('saveDraftHit').innerHTML = '草稿在' + date.toLocaleTimeString() + '保存成功';
            },
            error: function (data) {

            }
        });
    }
}

function editFile() {
    const fileContent = document.getElementById("fileContent").value;
    if (fileContent.length == 0) {
        alert('请输入文件内容');
        return;
    }
    if (preContent == fileContent) {
        alert('文件未修改');
        return;
    }
    if (fileId == null) {
        alert('文件不存在');
        return;
    }
    data = {
        fileId: fileId,
        fileContent: fileContent,
        editNumber: editNumber
    };
    $.ajax({
        url: httpUrl + 'updateFileContent',
        dataType: 'json',
        type: 'POST',
        data: data,
        success: function (data) {
            alert(data.msg);
            preContent = fileContent;
            window.location.href = 'view.html';
        },
        error: function (data) {

        }
    });
}

function startEdit() {
    const url = window.location.href;
    const params = url.split('=');
    let fileName = params[1];
    fileName = decodeURIComponent(fileName);
    $.ajax({
        url: httpUrl + 'queryFileInfo',
        dataType: 'json',
        data: {fileName: fileName},
        type: 'GET',
        success: function (data) {
            dealData(data.data);
        },
        error: function (data) {

        }
    });
}

function dealData(data) {
    if (data == null) {
        return;
    }
    fileHistory = data;
    const historySelect = document.getElementById("historySelect");
    for (let i = 0; i < data.length; i++) {
        const d = data[i];
        if (i == 0) {
            fileId = d.fileId;
            draft = d.fileContent;
            preContent = d.fileContent;
            document.getElementById("fileContent").value = d.fileContent;
        }
        historySelect.options.add(new Option(d.createTime, i));
    }
    window.setInterval(saveDraft, 5000);
    getEditNumber();
}

function getEditNumber() {
    $.ajax({
        url: httpUrl + 'getEditNumber',
        dataType: 'json',
        data: {fileId: fileId},
        type: 'GET',
        success: function (data) {
            editNumber = data.data;
            window.setInterval(canEdit, 1000);
        },
        error: function (data) {

        }
    });
}

function canEdit() {
    $.ajax({
        url: httpUrl + 'canEdit',
        dataType: 'json',
        data: {fileId: fileId, editNumber: editNumber},
        type: 'GET',
        success: function (data) {
            const editTime = data.data;
            if (editTime < 0) {
                document.getElementById('fileContent').disabled = false;
                document.getElementById('editBtn').disabled = false;
                document.getElementById('editHit').innerHTML = '当前编辑器剩余' + (60 + editTime) + '秒编辑时间';
            } else {
                document.getElementById('fileContent').disabled = true;
                document.getElementById('editBtn').disabled = true;
                document.getElementById('editHit').innerHTML = '当前编辑器在' + editTime + '秒后可编辑';
            }
        },
        error: function (data) {

        }
    });
}
