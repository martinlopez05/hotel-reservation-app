import { Mail, Github, Linkedin, Twitter } from "lucide-react"

export const CustomFooter = () => {
    const currentYear = new Date().getFullYear()

    return (
        <footer className="bg-muted border-t border-border mt-16">
            <div className="container mx-auto px-4 py-8">
                <div className="grid gap-8 md:grid-cols-3">
                    {/* Información de contacto */}
                    <div>
                        <h3 className="font-semibold text-foreground mb-4">Contacto</h3>
                        <div className="space-y-2">
                            <a
                                href="martinlopez45630@gmail.com"
                                className="flex items-center gap-2 text-sm text-muted-foreground hover:text-primary transition-colors"
                            >
                                <Mail className="w-4 h-4" />
                                <span>martinlopez45630@gmail.com</span>
                            </a>
                        </div>
                    </div>


                    <div>
                        <h3 className="font-semibold text-foreground mb-4">Enlaces</h3>
                        <div className="space-y-2">
                            <a href="/" className="block text-sm text-muted-foreground hover:text-primary transition-colors">
                                Inicio
                            </a>
                            <a href="/register" className="block text-sm text-muted-foreground hover:text-primary transition-colors">
                                Registrarse
                            </a>
                        </div>
                    </div>

                    {/* Redes sociales */}
                    <div>
                        <h3 className="font-semibold text-foreground mb-4">Síguenos</h3>
                        <div className="flex gap-4">
                            <a
                                href="https://github.com/martinlopez05"
                                target="_blank"
                                rel="noopener noreferrer"
                                className="bg-background p-2 rounded-lg hover:bg-primary hover:text-primary-foreground transition-colors"
                            >
                                <Github className="w-5 h-5" />
                            </a>
                            <a
                                href="https://www.linkedin.com/in/martin-lopez-8264132a8/"
                                target="_blank"
                                rel="noopener noreferrer"
                                className="bg-background p-2 rounded-lg hover:bg-primary hover:text-primary-foreground transition-colors"
                            >
                                <Linkedin className="w-5 h-5" />
                            </a>
                        </div>
                    </div>
                </div>

                {/* Copyright */}
                <div className="mt-8 pt-8 border-t border-border text-center">
                    <p className="text-sm text-muted-foreground">
                        © {currentYear} HotelFly. Todos los derechos reservados.
                    </p>
                </div>
            </div>
        </footer>
    )
}
