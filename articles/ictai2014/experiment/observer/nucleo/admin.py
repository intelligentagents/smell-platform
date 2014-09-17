from django.contrib import admin

from nucleo import models

admin.site.register(models.Project)
admin.site.register(models.Statement)
admin.site.register(models.Smell)
admin.site.register(models.Tool)
admin.site.register(models.ToolAnalysis)
admin.site.register(models.Human)
admin.site.register(models.HumanAnalysis)
