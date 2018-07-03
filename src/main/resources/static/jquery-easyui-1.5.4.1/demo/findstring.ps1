
$colltems = Get-ChildItem datagrid
foreach ($i in $colltems){
$filecontent = Get-Content $i.fullName | findstr /i "load"
write-host $filecontent $i.fullName
}