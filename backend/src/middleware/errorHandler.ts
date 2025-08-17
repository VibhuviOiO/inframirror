import { Prisma } from '@prisma/client';
import type { Request, Response, NextFunction } from 'express';

// Global error handler middleware
export function errorHandler(err: any, req: Request, res: Response, next: NextFunction) {
	// Prisma foreign key constraint violation
	if (err instanceof Prisma.PrismaClientKnownRequestError) {
		if (err.code === 'P2003') {
			return res.status(409).json({ error: 'Cannot delete: record is referenced by other data.' });
		}
		if (err.code === 'P2025') {
			return res.status(404).json({ error: 'Record not found.' });
		}
	}
	// Fallback for other errors
	res.status(500).json({ error: err.message || 'Internal server error' });
}
