@echo off
rem Generates a thumbnail image for a crawled document on Windows.
rem
rem Usage: generate-thumbnail.bat <type> <file> <output_file> [mimetype]
rem   type: msoffice, pdf, ps, svg or image
rem   file: a local file path (not a file: URL)
rem
rem Required tools (looked up in PATH and the default install locations;
rem the first available one is used):
rem   ImageMagick : magick.exe (ImageMagick 7)
rem   PDF         : pdftoppm.exe (Poppler) or gswin64c.exe (Ghostscript),
rem                 or ImageMagick with Ghostscript
rem   PostScript  : gswin64c.exe (Ghostscript)
rem   MS Office   : soffice.com/soffice.exe (LibreOffice), and a PDF tool
rem   SVG         : rsvg-convert.exe or ImageMagick
rem
rem Each tool can also be specified by the full path in the following
rem environment variables: FESS_MAGICK, FESS_PDFTOPPM, FESS_GS, FESS_SOFFICE
rem and FESS_RSVG_CONVERT.
rem
rem Note: convert.exe is not used because it conflicts with the Windows
rem system command of the same name.

setlocal EnableExtensions DisableDelayedExpansion

set "CMD_TYPE=%~1"
set "TARGET_FILE=%~2"
set "OUTPUT_FILE=%~3"
set "MIMETYPE=%~4"
set "IMAGE_SIZE=100x100"
set "WORK_DIR="
set "GS="
set "RC=1"

if "%CMD_TYPE%"=="" (
  set "ERR=No filetype."
  goto :fail
)
if /i "%CMD_TYPE%"=="msoffice" goto :type_msoffice
if /i "%CMD_TYPE%"=="pdf" goto :type_pdf
if /i "%CMD_TYPE%"=="ps" goto :type_ps
if /i "%CMD_TYPE%"=="svg" goto :type_svg
if /i "%CMD_TYPE%"=="image" goto :type_image
echo Unsupported type: "%CMD_TYPE%" 1>&2
goto :end

rem ---------------------------------------------------------------------------
:type_msoffice
call :find_magick || goto :end
call :find_soffice
if not defined SOFFICE (
  set "ERR=soffice (LibreOffice) does not exist."
  goto :fail
)
call :create_work_dir || goto :end
mkdir "%WORK_DIR%\soffice"
rem Use a dedicated profile so that concurrent executions do not conflict.
set "PROFILE_URL=file:///%WORK_DIR:\=/%/profile"
setlocal EnableDelayedExpansion
set "PROFILE_URL=!PROFILE_URL: =%%20!"
for /f "delims=" %%U in ("!PROFILE_URL!") do endlocal & set "PROFILE_URL=%%U"
"%SOFFICE%" --headless --norestore --nologo --nolockcheck "-env:UserInstallation=%PROFILE_URL%" --convert-to pdf --outdir "%WORK_DIR%\soffice" "%TARGET_FILE%" >nul 2>&1
set "PDF_FILE="
for %%F in ("%WORK_DIR%\soffice\*.pdf") do set "PDF_FILE=%%~fF"
if not defined PDF_FILE (
  set "ERR=soffice does not work."
  goto :fail
)
call :render_pdf "%PDF_FILE%" || goto :end
call :create_thumbnail "%PNG_FILE%"
goto :check_output

rem ---------------------------------------------------------------------------
:type_pdf
call :find_magick || goto :end
call :create_work_dir || goto :end
call :render_pdf "%TARGET_FILE%" || goto :end
call :create_thumbnail "%PNG_FILE%"
goto :check_output

rem ---------------------------------------------------------------------------
:type_ps
call :find_magick || goto :end
call :find_gs
if not defined GS (
  set "ERR=Ghostscript (gswin64c.exe) does not exist."
  goto :fail
)
call :create_work_dir || goto :end
set "PDF_FILE=%WORK_DIR%\thumbnail.pdf"
"%GS%" -q -dSAFER -dBATCH -dNOPAUSE -sDEVICE=pdfwrite "-sOutputFile=%PDF_FILE%" "%TARGET_FILE%"
if not exist "%PDF_FILE%" (
  set "ERR=Ghostscript does not work."
  goto :fail
)
call :render_pdf "%PDF_FILE%" || goto :end
call :create_thumbnail "%PNG_FILE%"
goto :check_output

rem ---------------------------------------------------------------------------
:type_svg
set "RSVG_CONVERT=%FESS_RSVG_CONVERT%"
if not defined RSVG_CONVERT call :find_in_path RSVG_CONVERT rsvg-convert.exe
if defined RSVG_CONVERT (
  "%RSVG_CONVERT%" -w 100 -h 100 --keep-aspect-ratio "%TARGET_FILE%" -o "%OUTPUT_FILE%"
  goto :check_output
)
call :find_magick || goto :end
call :create_thumbnail "svg:%TARGET_FILE%"
goto :check_output

rem ---------------------------------------------------------------------------
:type_image
call :find_magick || goto :end
rem Build format hint from MIME type for better ImageMagick compatibility
set "FORMAT_HINT="
if /i "%MIMETYPE%"=="image/gif" set "FORMAT_HINT=gif:"
if /i "%MIMETYPE%"=="image/tiff" set "FORMAT_HINT=tiff:"
if /i "%MIMETYPE%"=="image/png" set "FORMAT_HINT=png:"
if /i "%MIMETYPE%"=="image/jpeg" set "FORMAT_HINT=jpeg:"
if /i "%MIMETYPE%"=="image/bmp" set "FORMAT_HINT=bmp:"
if /i "%MIMETYPE%"=="image/x-windows-bmp" set "FORMAT_HINT=bmp:"
if /i "%MIMETYPE%"=="image/x-ms-bmp" set "FORMAT_HINT=bmp:"
if /i "%MIMETYPE%"=="image/vnd.adobe.photoshop" set "FORMAT_HINT=psd:"
if /i "%MIMETYPE%"=="image/photoshop" set "FORMAT_HINT=psd:"
if /i "%MIMETYPE%"=="application/x-photoshop" set "FORMAT_HINT=psd:"
if /i "%MIMETYPE%"=="application/photoshop" set "FORMAT_HINT=psd:"
call :create_thumbnail "%FORMAT_HINT%%TARGET_FILE%"
goto :check_output

rem ---------------------------------------------------------------------------
:check_output
if not exist "%OUTPUT_FILE%" (
  set "ERR=Thumbnail is not created."
  goto :fail
)
set "RC=0"
goto :end

:fail
echo %ERR% 1>&2
goto :end

:end
if defined WORK_DIR if exist "%WORK_DIR%" rmdir /s /q "%WORK_DIR%" >nul 2>&1
exit /b %RC%

rem ===========================================================================
rem Subroutines
rem ===========================================================================

rem Creates a thumbnail from the first frame/page/layer of the input image.
rem The input file must come before the -thumbnail operator for ImageMagick 7.
:create_thumbnail
"%MAGICK%" "%~1[0]" -auto-orient -thumbnail %IMAGE_SIZE% "%OUTPUT_FILE%"
exit /b 0

rem Renders the first page of a PDF file to %PNG_FILE%.
:render_pdf
set "PNG_FILE=%WORK_DIR%\page.png"
set "PDFTOPPM=%FESS_PDFTOPPM%"
if not defined PDFTOPPM call :find_in_path PDFTOPPM pdftoppm.exe
if defined PDFTOPPM (
  "%PDFTOPPM%" -png -singlefile "%~1" "%WORK_DIR%\page"
  goto :render_pdf_check
)
call :find_gs
if defined GS (
  "%GS%" -q -dSAFER -dBATCH -dNOPAUSE -sDEVICE=png16m -r150 -dFirstPage=1 -dLastPage=1 "-sOutputFile=%PNG_FILE%" "%~1"
  goto :render_pdf_check
)
rem ImageMagick reads PDF files with Ghostscript found in the registry.
"%MAGICK%" -density 150 "%~1[0]" "%PNG_FILE%"
:render_pdf_check
if not exist "%PNG_FILE%" (
  echo Failed to render the PDF file. pdftoppm.exe or Ghostscript is required. 1>&2
  exit /b 1
)
exit /b 0

:find_magick
set "MAGICK=%FESS_MAGICK%"
if not defined MAGICK call :find_in_path MAGICK magick.exe
if not defined MAGICK for /d %%D in ("%ProgramFiles%\ImageMagick-7*") do if exist "%%~fD\magick.exe" set "MAGICK=%%~fD\magick.exe"
if not defined MAGICK (
  echo ImageMagick ^(magick.exe^) does not exist. 1>&2
  exit /b 1
)
exit /b 0

:find_gs
if defined GS exit /b 0
set "GS=%FESS_GS%"
if not defined GS call :find_in_path GS gswin64c.exe gswin32c.exe
if not defined GS for /d %%D in ("%ProgramFiles%\gs\gs*") do if exist "%%~fD\bin\gswin64c.exe" set "GS=%%~fD\bin\gswin64c.exe"
exit /b 0

:find_soffice
set "SOFFICE=%FESS_SOFFICE%"
if not defined SOFFICE call :find_in_path SOFFICE soffice.com soffice.exe
if not defined SOFFICE if exist "%ProgramFiles%\LibreOffice\program\soffice.com" set "SOFFICE=%ProgramFiles%\LibreOffice\program\soffice.com"
if not defined SOFFICE if exist "%ProgramFiles%\LibreOffice\program\soffice.exe" set "SOFFICE=%ProgramFiles%\LibreOffice\program\soffice.exe"
exit /b 0

rem Usage: call :find_in_path VAR exe1 [exe2 ...]
rem Sets VAR to the full path of the first executable found in PATH.
:find_in_path
set "_FIND_VAR=%~1"
set "%_FIND_VAR%="
:find_in_path_next
shift
if "%~1"=="" exit /b 0
set "_FIND_PATH="
for %%X in ("%~1") do set "_FIND_PATH=%%~$PATH:X"
if defined _FIND_PATH (
  set "%_FIND_VAR%=%_FIND_PATH%"
  exit /b 0
)
goto :find_in_path_next

:create_work_dir
set "TMP_BASE=%TEMP%"
if not defined TMP_BASE set "TMP_BASE=%TMP%"
if not defined TMP_BASE set "TMP_BASE=%SystemRoot%\Temp"
:create_work_dir_retry
set "WORK_DIR=%TMP_BASE%\fess-thumbnail-%RANDOM%-%RANDOM%"
if exist "%WORK_DIR%" goto :create_work_dir_retry
mkdir "%WORK_DIR%" >nul 2>&1
if not exist "%WORK_DIR%" (
  set "WORK_DIR="
  echo Failed to create a work directory. 1>&2
  exit /b 1
)
exit /b 0
