var str = '';
str += '<div id="DivID">';
str += '<OBJECT id="WebOffice" width="100%" height="'+officeheight+'" classid="clsid:23739A7E-5741-4D1C-88D5-D50B18F7C347"  codebase="';
str += imatrixRoot + '/widgets/iWebOffice/iWebOffice2003.ocx#version=8,0,0,0" >';
str += '</object>';
str += '</div>';
document.write(str);