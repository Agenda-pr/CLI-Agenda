**Como** usuario de la agenda
**quiero** poder eliminar tareas
**para** mantener mi lista de tareas actualizada y libre de elementos innecesarios o completados que ya no requiero.

## Descripción

El sistema debe permitir al usuario eliminar una tarea registrada, seleccionándola desde la consola.
Una vez eliminada, la tarea debe desaparecer de la lista y del almacenamiento persistente (base de datos o archivo).

La capa de presentación (menú CLI) trabaja únicamente con DTOs (`TaskDto`); la entidad del modelo no se expone fuera del servicio.

## Criterios de aceptación

- El usuario puede seleccionar la tarea a eliminar mediante su identificador (UUID) o su número en la lista.
- Antes de eliminar, el sistema muestra la tarea seleccionada y solicita confirmación: "¿Está seguro de eliminar esta tarea? (S/N)".
- Una vez confirmada, la tarea se elimina de la persistencia (no vuelve a aparecer al reiniciar la aplicación).
- Si el identificador introducido no existe, se muestra: "No se encontró una tarea con ese ID."
- Si el identificador no tiene un formato UUID válido, se muestra un mensaje claro (por ejemplo: "El ID introducido no es válido.").
- Si el usuario cancela la eliminación, la tarea se conserva sin modificaciones.
- Tras la eliminación se muestra: "Tarea eliminada correctamente."

## Contexto técnico (DTOs)

Ya están implementados:

- `CreateTaskDto(String title, Priority priority, LocalDate dueDate)`: entrada para crear tareas (no interviene en este issue).
- `TaskDto(UUID id, String title, Priority priority, boolean completed, LocalDate dueDate, LocalDateTime createdAt)`: salida hacia el menú. Se usa para listar y para mostrar la tarea antes de confirmar.

> Nota: al ser el `id` un `UUID`, ya no se valida que sea numérico; se valida el formato UUID.

## Tareas

### Repositorio
- [ ] Reutilizar `buscarPorId(UUID id)` ([#17](https://github.com/Agenda-pr/CLI-Agenda/issues/17)).
- [ ] Crear `eliminar(UUID id)` si todavía no existe, devolviendo `boolean` (si la tarea existía).

### Servicio (`TaskService`)
- [ ] Crear `eliminarTarea(UUID id)`: busca la tarea por ID, la elimina del repositorio y lanza `TareaNoEncontradaException` si no existe.
- [ ] Crear (o reutilizar) un método que devuelva `TaskDto` por ID, para que el menú pueda mostrar la tarea antes de confirmar. El mapeo entidad → `TaskDto` se hace en el servicio.

### Menú / CLI
- [ ] Añadir la opción "Eliminar tarea" al menú interactivo.
- [ ] Mostrar el listado de tareas (`List<TaskDto>`, reutilizando [#14](https://github.com/Agenda-pr/CLI-Agenda/issues/14)) para que el usuario vea los IDs disponibles.
- [ ] Pedir el ID y parsearlo a `UUID`. Si el formato no es válido (`IllegalArgumentException`), mostrar un mensaje claro y volver a pedirlo o al menú.
- [ ] Mostrar el `TaskDto` seleccionado (título, prioridad, estado, fecha límite) y pedir confirmación: "¿Está seguro de eliminar esta tarea? (S/N)".
- [ ] Validar la confirmación: aceptar "S" o "N" (sin distinguir mayúsculas) y volver a preguntar si la entrada no es válida.
- [ ] Si el usuario cancela (N), no modificar nada y volver al menú.
- [ ] Si el usuario confirma (S), llamar a `eliminarTarea(id)` y mostrar "Tarea eliminada correctamente."
- [ ] Capturar `TareaNoEncontradaException` y mostrar "No se encontró una tarea con ese ID."

### Pruebas
- [ ] Tests unitarios de `TaskService.eliminarTarea` (existente / inexistente).
- [ ] Tests del repositorio: `eliminar` devuelve `true`/`false` según exista la tarea.
- [ ] Comprobar que la tarea eliminada no reaparece en el listado ni al reiniciar la aplicación (cuando haya persistencia real).
- [ ] Pruebas manuales:
    - eliminar confirmando
    - eliminar cancelando
    - ID inexistente (UUID válido que no existe)
    - ID con formato inválido (no UUID)
    - confirmación inválida (ni S ni N)
    - eliminar con la lista vacía
    - eliminar la única tarea existente

### Entrega
- [ ] Abrir PR y solicitar revisión.