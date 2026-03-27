<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Resultados del Examen</title>
    <style>
        body { font-family: Arial, sans-serif; color: #333; margin: 20px; }
        h1 { color: #0056b3; border-bottom: 2px solid #0056b3; padding-bottom: 10px; }
        .info-box { background-color: #f9f9f9; border: 1px solid #ddd; padding: 15px; margin-bottom: 20px; }
        .nota { font-size: 24px; font-weight: bold; color: #d9534f; }
        .nota-aprobado { color: #5cb85c; }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f4f4f4; }
        .correcta { color: green; font-weight: bold; }
        .incorrecta { color: red; font-weight: bold; }
    </style>
</head>
<body>

    <h1>Resultados del Examen</h1>
    
    <div class="info-box">
        <p><strong>Examen:</strong> ${intento.examen.titulo!''}</p>
        <p><strong>Alumno:</strong> ${intento.usuario.nombreUsuario!''} ${intento.usuario.apellidoUsuario!''}</p>
        <p><strong>Fecha de realización:</strong> ${fecha}</p>
        <p><strong>Nota Final:</strong> 
            <span class="nota <#if (intento.nota >= 5.0)>nota-aprobado</#if>">
                ${intento.nota}/10
            </span>
        </p>
    </div>

    <h3>Detalle de Respuestas</h3>
    <table>
        <thead>
            <tr>
                <th>Pregunta</th>
                <th>Tu Respuesta</th>
                <th>Resultado</th>
            </tr>
        </thead>
        <tbody>
            <#list intento.respuestas as respuesta>
            <tr>
                <td>${respuesta.pregunta.enunciado!''}</td>
                <td><#if respuesta.opcionSeleccionada??>${respuesta.opcionSeleccionada.texto!''}<#else>No respondida</#if></td>
                <td>
                    <#if respuesta.opcionSeleccionada?? && respuesta.opcionSeleccionada.esCorrecta>
                        <span style="color: green; font-weight: bold;">Correcta</span>
                    <#else>
                        <span style="color: red; font-weight: bold;">Incorrecta</span>
                    </#if>
                </td>
            </tr>
            </#list>
        </tbody>
    </table>

</body>
</html>