

import type React from "react"

import { use, useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Textarea } from "@/components/ui/textarea"
import { Star, Trash2 } from "lucide-react"
import type { ReviewResponse } from "../data/review.response"
import { UserContext } from "@/context/UserContext"
import type { ReviewRequest } from "../data/review.request"
import { toast } from "react-toastify"



interface HotelReviewsProps {
    hotelId: number
}

export function HotelReviews({ hotelId }: HotelReviewsProps) {
    const [rating, setRating] = useState(0)
    const [hoverRating, setHoverRating] = useState(0)
    const [comment, setComment] = useState<string>("")

    // Datos de ejemplo de comentarios
    const [reviews, setReviews] = useState<ReviewResponse[]>([]);

    const BASE_URL = import.meta.env.VITE_API_URL_REVIEW;

    const { user } = use(UserContext);


    useEffect(() => {
        fetchGetReviews()
    }, [])


    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (rating === 0 || comment.trim() === "") {
            toast.error("Por favor, selecciona una calificación y escribe un comentario");
            return;
        }

        const newReview: ReviewRequest = {
            hotelId,
            userId: user?.id,
            rating,
            commentary: comment,
        };

        const createdReview = await fetchPostReview(newReview);

        if (createdReview) {
            setReviews((prev) => [createdReview, ...prev]);
            toast.success("Comentario publicado correctamente");
            setRating(0);
            setComment("");
        } else {
            toast.error("Error al publicar comentario");
        }
    };

    const handleDeleteReview = async (reviewId: number) => {
        try {
            await fetch(`${BASE_URL}/${reviewId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${user.token}`,
                    "ngrok-skip-browser-warning": "true"
                }
            })
            setReviews((prevReviews) => prevReviews.filter(r => r.id !== reviewId));
            toast.success('comentario eliminado correctamente')

        } catch (error) {
            console.log(error);
            toast.error('error al eliminar comentario')
        }

    }




    const fetchPostReview = async (review: ReviewRequest): Promise<ReviewResponse | null> => {
        try {
            const response = await fetch(`${BASE_URL}`, {
                method: "POST",
                headers: {
                    'Authorization': `Bearer ${user?.token}`,
                    "Content-Type": "application/json",
                    "ngrok-skip-browser-warning": "true"
                },
                body: JSON.stringify(review),
            });

            if (!response.ok) {
                console.error("Error al crear la reseña:", response.statusText);
                return null;
            }

            const data: ReviewResponse = await response.json();
            return data;
        } catch (error) {
            console.error("Error en fetchPostReview:", error);
            return null;
        }
    };


    const fetchGetReviews = async () => {
        try {
            const response = await fetch(`${BASE_URL}/${hotelId}`, {
                headers: {
                    'Authorization': `Bearer ${user?.token}`,
                    "ngrok-skip-browser-warning": "true"
                }
            })
            if (response.ok) {
                const data = await response.json();
                setReviews(data);
            }
        } catch (error) {
            console.log(error);
        }

    }

    const renderStars = (currentRating: number, interactive = false) => {
        return (
            <div className="flex gap-1">
                {[1, 2, 3, 4, 5].map((star) => (
                    <button
                        key={star}
                        type="button"
                        onClick={() => interactive && setRating(star)}
                        onMouseEnter={() => interactive && setHoverRating(star)}
                        onMouseLeave={() => interactive && setHoverRating(0)}
                        disabled={!interactive}
                        className={`${interactive ? "cursor-pointer" : "cursor-default"} transition-colors`}
                    >
                        <Star
                            className={`w-5 h-5 ${star <= (interactive ? hoverRating || rating : currentRating)
                                ? "fill-yellow-400 text-yellow-400"
                                : "fill-none text-gray-300"
                                }`}
                        />
                    </button>
                ))}
            </div>
        )
    }

    return (
        <div className="space-y-8">
            <div>
                <h2 className="text-3xl font-bold text-foreground mb-6">Comentarios y valoraciones</h2>

                {/* Formulario para agregar comentario */}
                <Card className="mb-8">
                    <CardHeader>
                        <CardTitle>Deja tu opinión</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <div>
                                <label className="text-sm font-medium text-foreground mb-2 block">Tu calificación</label>
                                {renderStars(rating, true)}
                            </div>

                            <div>
                                <label htmlFor="comment" className="text-sm font-medium text-foreground mb-2 block">
                                    Tu comentario
                                </label>
                                <Textarea
                                    id="comment"
                                    placeholder="Cuéntanos sobre tu experiencia en este hotel..."
                                    value={comment}
                                    onChange={(e) => setComment(e.target.value)}
                                    rows={4}
                                    className="resize-none"
                                />
                            </div>

                            <Button type="submit" className="w-full">
                                Publicar comentario
                            </Button>
                        </form>
                    </CardContent>
                </Card>

                {/* Lista de comentarios */}
                <div className="space-y-4">
                    {reviews.map((review) => (
                        <Card key={review.id}>
                            <CardContent className="pt-6">
                                <div className="flex items-start justify-between mb-3">
                                    <div>
                                        <h3 className="font-semibold text-foreground">{review.username}</h3>
                                        <p className="text-sm text-muted-foreground">
                                            {new Date(review.createdAt).toLocaleDateString("es-ES", {
                                                year: "numeric",
                                                month: "long",
                                                day: "numeric",
                                            })}
                                        </p>
                                    </div>
                                    {renderStars(review.rating)}
                                    {user.username === review.username && (
                                        <Button
                                            variant="ghost"
                                            size="icon"
                                            onClick={() => handleDeleteReview(review.id)}
                                            className="text-destructive hover:text-destructive hover:bg-destructive/10"
                                        >
                                            <Trash2 className="w-4 h-4" />
                                        </Button>
                                    )}
                                </div>
                                <p className="text-foreground leading-relaxed">{review.commentary}</p>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            </div>
        </div>
    )
}
