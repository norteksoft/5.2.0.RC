
function Select( combo )
{
	var iIndex = $('#selectList').get(0).selectedIndex ;
	$('#selectList').get(0).selectedIndex= iIndex ;
	$("#txtText").attr("value",$('#selectList').find("option:selected").text());
	$("#txtVal").attr("value",$('#selectList').val());
}

function Add()
{
	$("#selectList").append("<option value='"+$("#txtVal").attr("value")+"'>"+$("#txtText").attr("value")+"</option>");  
	//$('#selectList').get(0).selectedIndex = $("#selectList option").length - 1 ;
	$("#txtText").attr("value","");
	$("#txtVal").attr("value","");

//	oTxtText.focus() ;
}

function Modify()
{
	var iIndex = $('#selectList').get(0).selectedIndex ;
	if ( iIndex < 0 ) return ;
	$("#selectList").find("option:selected").attr("text",HTMLEncode($("#txtText").attr("value")));
	$("#selectList").find("option:selected").attr("value",$("#txtVal").attr("value"));
	$("#txtText").attr("value","");
	$("#txtVal").attr("value","");

//	oTxtText.focus() ;
}

function Move( steps )
{
	ChangeOptionPosition( steps );
}

function Delete()
{
	RemoveSelectedOptions() ;
}

function SetSelectedValue()
{
	var iIndex = $('#selectList').get(0).selectedIndex ;
	if ( iIndex < 0 ) return ;

	$("#initSelectValue").attr("value",$("#selectList").find("option:selected").attr("value")) ;
}

// Moves the selected option by a number of steps (also negative)
function ChangeOptionPosition( steps )
{
	var iActualIndex = $('#selectList').get(0).selectedIndex ;
	if ( iActualIndex < 0 )
		return ;
	var iFinalIndex = iActualIndex + steps ;
	if ( iFinalIndex < 0 )
		iFinalIndex = 0 ;
	if ( iFinalIndex > ( $("#selectList option").length - 1 ) )
		iFinalIndex = $("#selectList option").length - 1 ;
	if ( iActualIndex == iFinalIndex )
		return ;
//	var oOption = $("#selectList option[index="+iActualIndex+"]");
	var oOption = $("#selectList option:selected");
	var sText	= HTMLDecode( oOption.attr("text") ) ;
	
	oOption.remove();
	
	oOption = AddComboOption( sText, sText,iFinalIndex ) ;
	oOption.selected = true ;
}

// Remove all selected options from a SELECT object
function RemoveSelectedOptions()
{
	var iSelectedIndex = $('#selectList').get(0).selectedIndex ;
	$("#txtText").attr("value","");
	$("#txtVal").attr("value","");
	$("#selectList option:selected").remove();
}

// Add a new option to a SELECT object (combo or list)
function AddComboOption(optionValue,optionText,position)
{
	var option ;
   var userAgent = window.navigator.userAgent;
   if (userAgent.indexOf("MSIE") > 0) {
        option = document.createElement("option");
        option.value = optionValue;
        option.innerText = optionText;
        document.getElementById( "selectList" ).insertBefore(option,document.getElementById( "selectList" ).options[position]);
    }else{
    	option =new Option(optionValue, optionText);
    	document.getElementById( "selectList" ).insertBefore(option, document.getElementById( "selectList" ).options[position]);
    }
   return option;
}


function HTMLEncode( text )
{
	if ( !text )
		return '' ;

	text = text.replace( /&/g, '&amp;' ) ;
	text = text.replace( /</g, '&lt;' ) ;
	text = text.replace( />/g, '&gt;' ) ;

	return text ;
}


function HTMLDecode( text )
{
	if ( !text )
		return '' ;

	text = text.replace( /&gt;/g, '>' ) ;
	text = text.replace( /&lt;/g, '<' ) ;
	text = text.replace( /&amp;/g, '&' ) ;

	return text ;
}